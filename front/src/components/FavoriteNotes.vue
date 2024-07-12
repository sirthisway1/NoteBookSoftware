<template>
    <div class="container">
      <h2>收藏笔记列表</h2>
      <ul>
        <li v-for="note in favoriteNotes" :key="note.noteId">
          {{ note.title }}
        </li>
      </ul>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        favoriteNotes: [],
      };
    },
    async created() {
      await this.fetchFavoriteNotes();
    },
    methods: {
      async fetchFavoriteNotes() {
        const response = await fetch('/api/notes/favorites', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        });
  
        const data = await response.json();
        if (data.code === 200) {
          this.favoriteNotes = data.data;
        } else {
          alert('获取收藏笔记列表失败');
        }
      },
    },
  };
  </script>
  <style>
</style>