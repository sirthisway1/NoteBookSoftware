<template>
  <div id="app">
    <!-- <NoteCreateTree/> -->
    <router-view></router-view>
  </div>

</template>

<script>
import NoteCreateTree from './components/NoteCreateTree.vue';
export default {
  name: 'App',
  components: {
    NoteCreateTree
  },
};
</script>

<style lang="scss">
* {
  padding: 0;
  margin: 0;
}
#app {
  height: 100vh;
  width: 100vw;
}

</style>

