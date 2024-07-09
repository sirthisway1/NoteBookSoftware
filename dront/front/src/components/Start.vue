<template>
    <div class="container">
      <div class="sidebar">
        <div class="sidebar-username" id="username">{{ username }}</div>
        <div class="sidebar-item start-button" @click="goToStart">
          <div class="icon-placeholder"><img src="/vue图片/图片1.png" alt="开始图标" class="icon-image">
          </div>
          <span>开始</span>
        </div>
        <div class="sidebar-item" @click="goToNotebook">笔记本</div>
        <div class="sidebar-item" @click="goToCommunity">发现社区</div>
        <!-- <div class="sidebar-item" @click="goToCommunity">标签管理</div> -->
      </div>
      <div class="main-content">
        <div class="top-content">
          <div class="top-left">
            <div class="button-container">
              <div class="button">新建笔记</div>
              <div class="dropdown">
                <div class="dropdown-item" @click="showCreateNoteModal">新建日常笔记</div>
                <div class="dropdown-item">SWOT模板思维笔记 @click=""</div>
                <div class="dropdown-item">5W1H模板思维笔记</div>
                <div class="dropdown-item">时间管理四象限模板思维笔记</div>
              </div>
            </div>
          </div>
          <div class="top-right">
            <div class="button" @click="showNewNotebookModal">新建笔记本</div>
          </div>
        </div>
        <div class="search-bar">
          <input type="text" v-model="searchInput" @input="search" :placeholder="searchMode === 'keyword' ? '关键字搜索' : '标签搜索'">
          
          <div class="toggle-button" @click="toggleSearchMode">
            {{ searchMode === 'keyword' ? '切换到标签搜索' : '切换到关键字搜索' }}
          </div>
        </div>
        <div class="bottom-content">
          <div class="note-list">
            <div v-for="note in filteredNotes" :key="note.noteId" class="note-item">
              <h3>{{ note.name }}</h3>
              <p>最后修改日期: {{ formatDate(note.lastModified) }}</p>
              <p>标签：
                <span v-for="(tag, index) in note.tags" :key="index" class="tag">
                  {{ tag }}{{ index < note.tags.length - 1 ? ', ' : '' }}
                </span>
              </p>
            </div>
          </div>
        </div>
      </div>
      <div class="side-buttons">
        <!-- <div class="side-button">查看收藏列表</div> -->
        <div class="side-button" @click="goToPrivate" >查看私密列表</div>
      </div>
  
      <!-- 新建笔记本的模态框 -->
      <div class="modal" v-if="isModalVisible">
        <div class="modal-content">
          <div class="modal-header">
            <h2>新建笔记本</h2>
            <span class="close" @click="hideNewNotebookModal">&times;</span>
          </div>
          <div class="modal-body">
            <h3>基本信息</h3>
            <div>
              <label>笔记本名称:</label>
              <input type="text" v-model="newNotebookName">
            </div>
            <div>
              <label>简介:</label>
              <textarea v-model="newNotebookDescription"></textarea>
            </div>
            <div class="modal-actions">
              <button @click="createNotebook">创建</button>
            </div>
          </div>
        </div>
      </div>
      <!-- 包含模态框组件 -->
      <CreateNoteModal :visible="isCreateNoteModalVisible" @close="hideCreateNoteModal" />
    </div>
  </template>
  
  <script>
  import CreateNoteModal from './CreateNoteModal.vue';
  export default {
    components: {
    CreateNoteModal,
  },
  data() {
      return {
        username: '测试用户', 
        isModalVisible: false,
        isCreateNoteModalVisible: false,
        newNotebookName: '',
        newNotebookDescription: '',
        notes:[
        {
          noteId: '1',
          name: '工作计划',
          lastModified: '2024-07-07T08:30:00Z',
          content: '这是工作计划的内容...',
          tags: ['工作', '会议']
        },
        {
          noteId: '2',
          name: '学习笔记',
          lastModified: '2024-07-06T15:45:00Z',
          content: '这是学习笔记的...',
          tags: ['学习', '编程']
        },
        {
          noteId: '3',
          name: '日记',
          lastModified: '2024-07-05T22:10:00Z',
          content: '这是日记的内容...',
          tags: ['生活', '购物']
        },
        {
          noteId: '4',
          name: '项目ideas',
          lastModified: '2024-07-04T11:20:00Z',
          content: '这是项目ideas的内容...',
          tags: ['旅行', '摄影']
        },
        {
          noteId: '5',
          name: '购物清单',
          lastModified: '2024-07-03T09:00:00Z',
          content: '这是购物清单的内容...',
          tags: ['美食', '烹饪']
        }
        ], //存储笔记列表
        searchKeyword: '',  //搜索关键字
        filteredNotes: [],  //符合的笔记
        searchInput:'',
        searchMode: 'keyword', // 'keyword' 或 'tag'
      };
    },
    methods: {
      goToStart() {
        this.$router.push({ name: 'Start' });
      },
      goToNotebook() {
        this.$router.push({ name: 'Notebook' }); // 这里需要在路由中添加对应路径
      },
      goToCommunity() {
        this.$router.push({ name: 'Community' }); // 这里需要在路由中添加对应路径
      },
    //   goToFavorites() {
    //   this.$router.push({ name: 'FavoriteNotes' });
    // },
    goToPrivate() {
      this.$router.push({ name: 'PrivateNotes' });
    },
    toggleSearchMode() {
        this.searchMode = this.searchMode === 'keyword' ? 'tag' : 'keyword';
        this.searchInput = ''; // 清空搜索输入
        this.search(); // 重置搜索结果
      },
      search() {
        const searchTerm = this.searchInput.toLowerCase().trim();
        
        if (!searchTerm) {
          this.filteredNotes = this.notes; // 如果搜索输入为空，显示所有笔记
          return;
        }

        if (this.searchMode === 'keyword') {
          // 关键字搜索
          this.filteredNotes = this.notes.filter(note => 
            note.name.toLowerCase().includes(searchTerm) || 
            note.content.toLowerCase().includes(searchTerm)
          );
        } else {
          // 标签搜索
          this.filteredNotes = this.notes.filter(note => 
            note.tags.some(tag => tag.toLowerCase().includes(searchTerm))
          );
        }
      },
      showCreateNoteModal() {
      this.isCreateNoteModalVisible = true;
    },
    hideCreateNoteModal() {
      this.isCreateNoteModalVisible = false;
    },
      showNewNotebookModal() {
        this.isModalVisible = true;
      },
      hideNewNotebookModal() {
        this.isModalVisible = false;
      },
      async createNotebook() {
        const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          return;
        }
  
        const response = await fetch('/api/notebooks/createNotebooks', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            name: this.newNotebookName,
            summary: this.newNotebookDescription,
          })
        });
  
        const data = await response.json();
        if (data.code === 200) {
          alert('笔记本创建成功');
          this.hideNewNotebookModal();
          // 处理笔记本创建成功后的逻辑
        } else if (data.code === 501) {
          alert('token验证失败');
        } else {
          alert('创建笔记本失败');
        }
      },

/*模拟时不需要*/ 
      // async search() {
      //   if (!this.searchKeyword.trim()) {
      //     this.fetchNotes(); // 如果搜索框为空,获取所有笔记
      //     return;
      //   }

      //   const token = localStorage.getItem('token');
      //   if (!token) {
      //     alert('请先登录');
      //     return;
      //   }

      //   try {
      //     const response = await fetch(`/api/notes?keyword=${encodeURIComponent(this.searchKeyword)}`, {
      //       method: 'GET',
      //       headers: {
      //         'Authorization': `Bearer ${token}`
      //       }
      //     });

      //     const data = await response.json();
      //     if (data.code === 200) {
      //       this.filteredNotes = data.data;
      //     } else if (data.code === 501) {
      //       alert('token验证失败');
      //     } else if (data.code === 407) {
      //       alert('没有找到匹配的笔记');
      //       this.filteredNotes = [];
      //     } else {
      //       alert('搜索失败');
      //     }
      //   } catch (error) {
      //     console.error('搜索笔记时出错:', error);
      //     alert('搜索失败');
      //   }
      // },

      // async fetchNotes() {
      // const token = localStorage.getItem('token');
      // if (!token) {
      //   alert('请先登录');
      //   return;
      // }

      // try {
      //   // 获取所有笔记ID
      //   const response = await fetch('/api/notes', {
      //     method: 'GET',
      //     headers: {
      //       'Authorization': `Bearer ${token}`
      //     }
      //   });

      //   const data = await response.json();
      //   if (data.code === 200) {
      //     // 获取每个笔记的详细信息
      //     const notePromises = data.data.noteId.map(id => this.fetchNoteDetails(id));
      //     this.notes = await Promise.all(notePromises);
      //   } else if (data.code === 501) {
      //     alert('token验证失败');
      //   } else {
      //     alert('获取笔记列表失败');
      //   }
      // } catch (error) {
      //   console.error('获取笔记列表时出错:', error);
      //   alert('获取笔记列表失败');
      // }
      // },

      // async fetchNoteDetails(noteId) {
      //   const token = localStorage.getItem('token');
      //   const response = await fetch(`/api/${noteId}`, {
      //     method: 'GET',
      //     headers: {
      //       'Authorization': `Bearer ${token}`
      //     }
      //   });

      //   const data = await response.json();
      //   if (data.code === 200) {
      //     return data.data;
      //   } else {
      //     console.error(`获取笔记 ${noteId} 详情失败`);
      //     return null;
      //   }
      // },

      // formatDate(dateString) {
      // const date = new Date(dateString);
      // return date.toLocaleString(); // 格式化日期
      // }

      formatDate(dateString) {   //模拟formatDate
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
    watch: {
      searchKeyword() {
        this.keywordSearch();
      }
    },
    //模拟时不需要
    mounted() {
      // this.fetchNotes(); // 组件挂载时获取笔记列表
      this.filteredNotes = this.notes;
    }

    
  };
  </script>
  
<style scoped>
  .container {
  display: flex;
  height: 100vh;
  width: 100%;
  position: fixed;
  top: 0;
  left: 0;
}
  
.sidebar {
  width: 200px;
  height: 100%;
  position: fixed;
  top: 0;
  left: 0;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  border-right: 1px solid #e0e0e0;
  background-color: #ffffff;
  text-align: center;
  padding-top: 20px;
  box-shadow: 2px 0 5px rgba(0,0,0,0.1);
  z-index: 10;
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
    width: 60px; /* 设置圆形宽度 */
    height: 60px; /* 设置圆形高度 */
    background-color: #87d37c; /* 设置圆形背景颜色 */
    border-radius: 50%; /* 使元素为圆形 */
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 10px; /* 圆形与文字之间的间距 */
}

.icon-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 确保图片填满圆形且不变形 */
}

.sidebar-item span {
    font-size: 14px; /* 设置文字大小 */
}

/* 选中效果 */
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
  margin-left: 200px; /* 与sidebar宽度相同 */
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  padding-left: 20px;
  overflow-y: auto;
  height: 100vh;
}
  
.top-content {
    height: 7%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px;
  }
  
  .top-left, .top-right {
    position: relative; /* 相对定位 */
    background-color: #87d37c;
  }
  .button-container {
    position: relative; /* 相对定位 */
    background-color: #87d37c;
  }
  
  .button {
    background-color: #87d37c;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 150px;
    height: 50px;
    color: rgb(0, 0, 0);
    border-radius: 25px;
    cursor: pointer;
    transition: var(--transition);
  }

  
  .dropdown {
    display: none; /* 默认隐藏 */
    position: absolute; /* 绝对定位 */
    top: 100%; /* 紧挨着按钮下面 */
    left: 0;
    width: 100%; /* 与按钮同宽 */
    background-color: #e0e0e0;
    border-radius: 5px;
    flex-direction: column;
    z-index: 1; /* 确保下拉框在前面 */
    
  }
  
  .dropdown-item {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 150px; /* 与按钮同宽 */
    height: 60px; /* 与按钮同高 */
    border-top: 1px solid #ccc;
    cursor: pointer;
  }
  
  .button-container:hover .dropdown {

    display: flex; /* 鼠标悬停时显示下拉框 */
  }
  
  .search-bar {
    height: 50px;
    display: flex;
    align-items: center;
    padding: 0 20px;
    position: relative;
  }
  
  .search-bar input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    margin-top: 25px;
  }
  
  .search-button {
    width: 20px;
    height: 20px;
    background-color: red;
    border-radius: 50%;
    position: absolute;
    right: 30px;
    cursor: pointer;
    margin-top: 25px;
  }
  
  .toggle-button {
  margin-left: 10px;
  padding: 5px 10px;
  background-color: #f0f0f0;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}

  .toggle-button:hover {
  background-color: #e0e0e0;
}

.bottom-content {
    margin-top: 20px;
    padding: 0 20px;
    flex-grow: 1; /* 让 bottom-content 占据剩余空间 */
    display: flex;
    position: relative;
  }
  
  .note-list {
    flex-grow: 1; /* 让 note-list 占据剩余空间 */
    background-color: #d3d3d3;
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
    padding:20px;
    align-items: center;
    
    margin-bottom: 30px;
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(0,0,0,0.1);
  }
  
  .side-buttons {
    width: 150px; /* 调整按钮宽度 */
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    padding-right: 20px;
    padding-top: 200px; /* 增加左边间隔 */
  }
  
  .side-button {
    background-color: #8b4513;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    padding: 10px; /* 调整按钮内边距 */
    margin: 5px 0; /* 调整按钮间距 */
    cursor: pointer;
    border-radius: 5px;
    font-size: 1em; /* 调整按钮字体大小 */
    height: 50px; /* 调整按钮高度 */
  }
  
  .modal {
    display: flex;
    position: fixed;
    z-index: 1;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    overflow: auto;
    background-color: rgba(0,0,0,0.4);
    justify-content: center;
    align-items: center;
  }
  
  /* 新建笔记本UI*/
  .modal-content {
    background-color: #fefefe;
    padding: 20px;
    border: 1px solid #888;
    width: 40%;
    border-radius: 10px;
  }
  
  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 25%;
  }
  
  .modal-header h2 {
    margin: 0;
    flex: 1;
  }
  
  
  .modal-header .close {
    color: #aaa;
    font-size: 28px;
    font-weight: bold;
    cursor: pointer;
  }
  
  .modal-header .close:hover,
  .modal-header .close:focus {
    color: black;
    text-decoration: none;
    cursor: pointer;
  }
  
  .modal-body {
    margin-top: 10px;
  }
  
  .modal-body h3 {
    margin: 10px 0;
  }
  
  .modal-body div {
    margin-bottom: 10px;
  }
  
  .modal-body label {
    display: block;
    margin-bottom: 5px;
  }
  
  .modal-body input, .modal-body textarea {
    width: 100%;
    padding: 8px;
    box-sizing: border-box;
  }
  
  .modal-body textarea {
    height: 100px; /* 设置简介文本框的高度 */
    resize: none;
  }
  
  .modal-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;
  }
  
  .modal-actions button {
    padding: 10px 20px;
    font-size: 1rem;
    cursor: pointer;
  }

  .note-item {
    background-color: white;
    border-radius: 10px;
    padding: 20px;
    margin-bottom: 20px;
    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    transition: var(--transition);
    width: calc(33.333% - 20px);
  }

  .note-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0,0,0,0.2);
  }

  .note-item h3 {
    margin: 0 0 10px 0;
    color: var(--primary-color);
  }

  .note-item p {
    margin: 0;
    font-size: 0.9em;
    color: #666;
  }
</style>
  