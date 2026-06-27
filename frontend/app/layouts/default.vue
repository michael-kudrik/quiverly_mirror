<template>
  <div class="min-h-screen flex flex-col">
    <Navbar class="w-full" />

    <main class="flex-1 w-full">
      <slot />
    </main>
  <footer class="footer sm:footer-horizontal footer-center bg-base-200 text-base-content p-1">
    <aside>
      <p>{{new Date().getFullYear()}} - Michael Kudrik</p>

    </aside>
  </footer>
  </div>
</template>