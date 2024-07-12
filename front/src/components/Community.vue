<!-- 已测试接口 -->
<template>
    <div class="container">
      <div class="sidebar">
        <div class="sidebar-item" id="username">{{ username }}</div>
        <div class="sidebar-item" @click="goToStart">开始</div>
        <div class="sidebar-item" @click="goToNotebook">笔记本</div>
        <div class="sidebar-item community-button" @click="goToCommunity">
          <div class="icon-placeholder"><img src="/vue图片/图片3.png" alt="开始图标" class="icon-image">
          </div>
          <span>发现社区</span>
        </div>
        <!-- <div class="sidebar-item" @click="goToTags">标签管理</div> -->
      </div>
      <div class="main-content">
        <div class="community">
          <h1>发现社区</h1>
        </div>
        <div class="note-container">
          <div class="note" v-for="note in filteredNotes" :key="note.noteId" @click="goToNoteDetail(note.noteId)">
            <div class="note-header">
              <div class="note-user">用户名: {{ note.username }}</div>
              <div class="note-title">{{ note.title }}</div>
            </div>
            <div class="note-info">
              <div class="note-time">创建时间: {{ formatDate(note.createdAt) }}</div>
            </div>
            <div class="note-content">{{ truncateContent(note.content) }}</div>
          </div>
        </div>
    </div>
    </div>
</template>
  
  
  
<script>
import axios from 'axios';

export default {
  data() {
    return {
      username: '这里填写用户名',
      notes: [], // 存储所有公共笔记
    };
  },
  computed: {
    filteredNotes() {
      return this.notes.filter((note) => note.type !== 1 && !note.isPrivate);
    },
  },
  methods: {
    goToStart() {
      this.$router.push({ name: 'Start' });
    },
    goToNotebook() {
      this.$router.push({ name: 'Notebook' });
    },
    goToCommunity() {
      this.$router.push({ name: 'Community' });
    },
    goToTags() {
      this.$router.push({ name: 'Tags' });
    },
    goToNoteDetail(noteId) {
      this.$router.push({ name: 'CommunityDetail', params: { noteId: noteId } });
    },

    async fetchNotes() {
      try {
        const response = await axios.get('/api/community-notes');
        this.notes = response.data.data;
      } catch (error) {
        console.error("Error fetching notes:", error);
      }
    },
    truncateContent(content) {
      const maxLength = 400;
      if (content.length <= maxLength) {
        return content;
      }
      return content.substring(0, maxLength) + '...';
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN', { 
        year: 'numeric', 
        month: '2-digit', 
        day: '2-digit', 
        hour: '2-digit', 
        minute: '2-digit' 
      });
    }
  },
  async mounted() {
    await this.fetchNotes();
  },
};
</script>


  
<style scoped>
.container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: 200px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  border-right: 1px solid #e0e0e0;
  background-color: #ffffff;
  text-align: center;
  padding-top: 20px;
  box-shadow: 2px 0 5px rgba(0,0,0,0.1);
}

.sidebar-item {
  height: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px 0;
  cursor: pointer;
  color: #333;
  transition: background-color 0.3s;
}

.sidebar-item:hover {
  background-color: #f0f0f0;
}

.community-button {
  background-color: #4CAF50;
  color: white;
  border-radius: 20px;
  padding: 10px 20px;
  margin: 10px 20px;
  transition: background-color 0.3s;
}

.icon-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 确保图片填满圆形且不变形 */
}

.community-button:hover {
  background-color: #45a049;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.community {
  padding: 20px;
  flex-shrink: 0;
}

.note-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.note {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  width: 80%;
  height: 220px; /* 固定高度 */
  max-width: 800px; /* 设置最大宽度 */
  overflow: hidden;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  margin-left: auto;
  margin-right: auto;
}

.note-header {
  background-color: #87d37c;
  padding: 10px;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

.note-content {
  background-color: #ffffff;
  padding: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  flex-grow: 1;
}

.note-info {
  padding: 5px 10px;
  background-color: #f0f0f0;
  font-size: 0.8em;
  color: #666;
}

.note-time {
  font-style: italic;
}
</style>