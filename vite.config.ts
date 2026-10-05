import { defineConfig } from 'vite'

// Slidev's generated CSS trips lightningcss's minifier (seen with Slidev 52/53 on 5 Oct 2026).
// The dev server never minifies, so this only affects `slidev build`.
export default defineConfig({
  build: { cssMinify: false },
})
