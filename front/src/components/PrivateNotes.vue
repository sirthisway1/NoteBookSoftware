<!-- 已测试接口 -->
<template>
  <div class="container">
    <div class="close-button" @click="goToStart">×</div>
    <div class="sidebar">
      <div class="sidebar-username" id="username">{{ username }}</div>
      <div class="sidebar-item start-button" @click="goToStart">
        <div class="icon-placeholder"><img src="/vue图片/图片1.png" alt="开始图标" class="icon-image"></div>
        <span>开始</span>
      </div>
      <div class="sidebar-item" @click="goToNotebook">笔记本</div>
      <div class="sidebar-item" @click="goToCommunity">发现社区</div>
    </div>
    <div class="main-content">
      <div class="top-content">
        <h2>私密笔记列表</h2>
      </div>
      
      <div class="bottom-content">
        <div class="note-list">
          <div v-for="note in privateNotes" :key="note.noteId" class="note-item">
            <h3>{{ note.title }}</h3>
            <p>标签: {{ note.tags.join(', ') }}</p>
            <p>最后修改时间: {{ formatDate(note.lastModified) }}</p>
          </div>
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
      username: 'User',
      privateNotes: [],
    };
  },
  created() {
    this.fetchPrivateNotes();
  },
  methods: {
    async fetchPrivateNotes() {
      try {
        // 假设你已经在登录时存储了 token
        const token = localStorage.getItem('token');
        const response = await axios.get('http://localhost:3000/api/notes', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        if (response.data.code === 200) {
          this.privateNotes = response.data.data.filter(note => note.isPrivate);
        } else {
          console.error('获取笔记列表失败:', response.data.message);
        }
      } catch (error) {
        console.error('获取笔记列表出错:', error);
      }
    },
    goToStart() {
      this.$router.push({ name: 'Start' });
    },
    goToNotebook() {
      // 实现跳转到笔记本页面的逻辑
    },
    goToCommunity() {
      // 实现跳转到发现社区页面的逻辑
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString(); // 你可以根据需要自定义日期格式
    }
  },
};
</script>

  <style scoped>
  .container {
    display: flex;
    height: 100vh;
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
  
  .sidebar-username {
    height: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 15px 0;
    font-weight: bold;
    margin-bottom: 20px;
  }
  
  .icon-placeholder {
    width: 60px;
    height: 60px;
    background-color: #87d37c;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 10px;
  }

  .icon-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 确保图片填满圆形且不变形 */
}

  
  .sidebar-item span {
    font-size: 14px;
  }
  
  .start-button {
    background-color: #4CAF50;
    color: white;
    border-radius: 20px;
    padding: 10px 20px;
    margin: 10px 20px;
    transition: background-color 0.3s;
  }
  
  .start-button:hover {
    background-color: #45a049;
  }
  
  .main-content {
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
  }
  
  .top-content {
    margin-bottom: 20px;
  }
  
  .search-bar {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .search-bar input {
    flex-grow: 1;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
  }
  
  .search-button {
    width: 20px;
    height: 20px;
    background-color: #4CAF50;
    border-radius: 50%;
    margin-left: 10px;
    cursor: pointer;
  }
  
  .note-list {
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
  }
  
  .note-item {
    background-color: white;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    transition: transform 0.3s, box-shadow 0.3s;
    width: calc(33.333% - 20px);
  }
  
  .note-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0,0,0,0.2);
  }
  
  .note-item h3 {
    margin: 0 0 10px 0;
    color: #4CAF50;
  }
  
  .note-item p {
    margin: 0;
    font-size: 0.9em;
    color: #666;
  }

  .close-button {
  position: absolute;
  top: 20px;
  right: 20px;
  font-size: 24px;
  cursor: pointer;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
  border-radius: 50%;
  transition: background-color 0.3s;
}

.close-button:hover {
  background-color: #e0e0e0;
}
  </style>