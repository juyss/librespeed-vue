const { createApp, ref } = Vue

createApp({
  setup() {
    const running = ref(false)
    const progress = ref(0)
    const downloadMbps = ref(0)
    const uploadMbps = ref(0)
    const pingMs = ref(0)
    const jitterMs = ref(0)
    const ip = ref('')
    let aborts = []

    function formatMbps(v) {
      if (!v || v < 0.01) return '0.00'
      return v.toFixed(2)
    }
    function formatMs(v) {
      if (!v || v < 0.01) return '0.00'
      return v.toFixed(2)
    }

    async function getConfig() {
      const r = await fetch('/config')
      return await r.json()
    }
    async function getIp() {
      const r = await fetch('/ip?x=' + Math.random())
      const j = await r.json()
      ip.value = j.ip || ''
    }

    async function measurePing(count) {
      let times = []
      for (let i = 0; i < count; i++) {
        const t0 = performance.now()
        await fetch('/ip?x=' + Math.random(), { cache: 'no-store' })
        const t1 = performance.now()
        times.push(t1 - t0)
      }
      const avg = times.reduce((a, b) => a + b, 0) / times.length
      const mean = avg
      const variance = times.reduce((a, b) => a + Math.pow(b - mean, 2), 0) / times.length
      pingMs.value = avg
      jitterMs.value = Math.sqrt(variance)
    }

    async function measureDownload(durationMs) {
      const controller = new AbortController()
      aborts.push(controller)
      const t0 = performance.now()
      let received = 0
      const timer = setTimeout(() => controller.abort(), durationMs)
      try {
        const res = await fetch('/garbage?x=' + Math.random(), { signal: controller.signal, cache: 'no-store' })
        const reader = res.body.getReader()
        while (true) {
          try {
            const { done, value } = await reader.read()
            if (done) break
            received += value.byteLength
            const elapsed = performance.now() - t0
            const seconds = elapsed / 1000
            if (seconds > 0) downloadMbps.value = (received * 8) / seconds / 1e6
            progress.value = Math.min(100, Math.round((elapsed / durationMs) * 100))
          } catch (e) {
            if (controller.signal.aborted) break
            throw e
          }
        }
      } catch (e) {
        if (!controller.signal.aborted) {
          // 网络错误，保留当前统计并继续流程
        }
      } finally {
        clearTimeout(timer)
        const seconds = (performance.now() - t0) / 1000
        if (seconds > 0) downloadMbps.value = (received * 8) / seconds / 1e6
      }
    }

    function fillRandom(buf) {
      const max = 65536
      for (let i = 0; i < buf.length; i += max) {
        crypto.getRandomValues(buf.subarray(i, Math.min(i + max, buf.length)))
      }
    }

    async function measureUpload(durationMs, chunkSize = 1024 * 1024, parallel = 2) {
      const t0 = performance.now()
      let totalSent = 0
      let stopped = false
      function timeLeft() { return durationMs - (performance.now() - t0) }

      async function sendChunk() {
        if (stopped) return
        const controller = new AbortController()
        aborts.push(controller)
        const buf = new Uint8Array(chunkSize)
        fillRandom(buf)
        const start = performance.now()
        try {
          const res = await fetch('/upload?x=' + Math.random(), { method: 'POST', body: buf, signal: controller.signal })
          const j = await res.json()
          totalSent += j.received || buf.byteLength
        } catch (_) {
          // 忽略中断或网络错误，继续统计
        }
        const elapsed = performance.now() - t0
        const seconds = elapsed / 1000
        if (seconds > 0) uploadMbps.value = (totalSent * 8) / seconds / 1e6
        progress.value = Math.min(100, Math.round((elapsed / durationMs) * 100))
        if (timeLeft() > 0) {
          // 继续发送下一块
          sendChunk()
        }
      }

      // 并发发送
      for (let i = 0; i < parallel; i++) sendChunk()

      // 等待到时
      while (timeLeft() > 0) {
        await new Promise(r => setTimeout(r, 100))
      }
      stopped = true
    }

    async function start() {
      running.value = true
      progress.value = 0
      downloadMbps.value = 0
      uploadMbps.value = 0
      pingMs.value = 0
      jitterMs.value = 0
      await getIp()
      const cfg = await getConfig()
      await measurePing(cfg.pingCount || 10)
      await measureDownload(cfg.durationMs || 8000)
      await measureUpload(cfg.durationMs || 8000, 1024 * 1024, 2)
      running.value = false
      progress.value = 100
    }

    function stop() {
      for (const c of aborts) c.abort()
      aborts = []
      running.value = false
    }

    return { running, progress, downloadMbps, uploadMbps, pingMs, jitterMs, ip, start, stop, formatMbps, formatMs }
  }
}).mount('#app')