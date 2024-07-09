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
      <!-- <div class="sidebar-item" @click="goToCommunity">标签管理</div> -->
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
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
  
  <script>
  export default {
    data() {
    return {
      username: 'User', // 假设用户名
      searchKeyword: '',
      privateNotes: [
        { noteId: '1', title: '笔记1', content: '笔记1的内容', tags: ['工作', '会议'] },
        { noteId: '2', title: '笔记2', content: '笔记2的内容', tags: ['学习', '编程'] },
        { noteId: '3', title: '笔记3', content: '笔记3的内容', tags: ['生活', '购物'] },
        { noteId: '4', title: '笔记4', content: '笔记4的内容', tags: ['旅行', '摄影'] },
      ],
    };
  },
    // async created() {
    //   await this.fetchPrivateNotes();
    // },
    methods: {
      // async fetchPrivateNotes() {
      //   const response = await fetch('/api/notes/private', {
      //     method: 'GET',
      //     headers: {
      //       'Content-Type': 'application/json',
      //     },
      //   });
  
      //   const data = await response.json();
      //   if (data.code === 200) {
      //     this.privateNotes = data.data;
      //   } else {
      //     alert('获取私密笔记列表失败');
      //   }
      // },

      goToStart() {
      this.$router.push({ name: 'Start' });
      },
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