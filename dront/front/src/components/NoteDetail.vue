<template>
  <div class="container">
    <!-- 左侧侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-item" id="username">{{ username }}</div>
      <div class="sidebar-item" @click="goToStart">开始</div>
      <div class="sidebar-item notebook-button">
        <div class="icon-placeholder"><img src="/vue图片/图片2.png" alt="开始图标" class="icon-image"></div>
        <span>笔记本</span>
      </div>
      <div class="sidebar-item" @click="goToCommunity">发现社区</div>
      <!-- <div class="sidebar-item" @click="goToCommunity">标签管理</div> -->
    </div>
    
    <!-- 中间笔记本内容区 -->
    <div class="middle-section">
      <div class="notebook-header">
        <!-- 笔记本标题 -->
        <h2>{{ notebookName }}</h2>
        <!-- 笔记数量 -->
        <div class="note-count-container">
          <p class="note-count">{{ notes.length }}条笔记</p>
          <i class="fas fa-plus add-note" @click="showCreateNoteModal"></i>
        </div>
        <!-- 搜索框 -->
        <div class="search-container">
          <i class="fas fa-search search-icon" @click="searchNotes"></i>
          <input type="text" v-model="searchQuery"/>
          <i class="fas fa-times clear-icon" @click="clearSearch"></i>
        </div>
      </div>
      <!-- 操作按钮 -->
      <button class="action-button" @click="backToStart">返回首页</button>
      <button class="action-button" @click="backToNotebook">目录</button>

      <!-- 笔记列表 -->
      <div class="notebook-list">
        <div v-for="(note, index) in notes" :key="index" class="note-item" :class="{ selected: selectedNote === note }" @click="selectNote(note)">
          {{ note.title }}
        </div>
        <!-- 若有更多笔记，添加翻页功能 -->
        <div v-if="hasMoreNotes" class="pagination">
          <button @click="prevPage">上一页</button>
          <button @click="nextPage">下一页</button>
        </div>
      </div>
    </div>

    <!-- 右侧笔记详情区 -->
    <div class="right-section">
      <div class="note-summary">
        <!-- 简介 -->
        <h3>简介</h3>
        <div class="summary-content">{{ notebookSummary }}</div>
      </div>

      <!-- 笔记内容 -->
      <div class="note-content">
        <div class="content-header">
        <h3>笔记内容</h3>
        <button v-if="selectedNote" class="edit-button" @click="editNote">编辑</button>
      </div>

        <div class="content-area">
          <template v-if="noteType === 1">
          {{ noteContent }}
          </template>
          <template v-else-if="noteType === 0">
          <!-- <NoteCreateTree  :readOnly="true" :initialTree="noteContent" /> -->
          </template>
        </div>
      </div>
      
     
    </div>

    <!-- 包含模态框组件 -->
    <CreateNoteModal :visible="isCreateNoteModalVisible" @close="hideCreateNoteModal" />
  </div>
</template>
  
  
  <script>
 

  //测试代码
import CreateNoteModal from './CreateNoteModal.vue';
import NoteCreateTree from './NoteCreateTree.vue';


export default {
  components: {
    CreateNoteModal,
    NoteCreateTree
  },
  data() {
    return {
      //测试数据
      //模拟笔记
      notes: [
        {noteId: '1', title: '笔记1', type:0,
        content: JSON.stringify({
        "resId": 0,
        "parentId": null,
        "resName": "时间管理四象限分析",
        "extend": true,
        "children": [
          {"resId": 1, "parentId": 0, "resName": "第一象限：紧急且重要", "extend": true, "children": []},
          {"resId": 2, "parentId": 0, "resName": "第二象限：不紧急但重要", "extend": true, "children": []},
          {"resId": 3, "parentId": 0, "resName": "第三象限：紧急但不重要", "extend": true, "children": []},
          {"resId": 4, "parentId": 0, "resName": "第四象限：不紧急且不重要", "extend": true, "children": []}
        ]
      }),
      tags: ['工作', '会议']
      },

        {noteId: '2', title: '笔记2', type:1,content: '笔记2的内容' , tags: ['学习', '编程']},
        {noteId: '3', title: '笔记3', type:1,content: '笔记3的内容', tags: ['生活', '购物']},
        {noteId: '4', title: '笔记4', type:1,content: '笔记4的内容', tags: ['旅行', '摄影'] },
        {noteId: '5', title: '笔记5', type:1,content: '笔记5的内容', tags: ['美食', '烹饪']},
        {noteId: '6', title: '笔记6', type:1,content: '笔记6的内容', tags: ['健康', '运动']  },
      ],
      selectedNote: null,
      hasMoreNotes: false, // 设置翻页功能的状态
      searchQuery: '', // 添加 searchQuery 属性
      notebookSummary: '这里是简介',
      notebookName: '这里是笔记本名',
      isCreateNoteModalVisible: false,
      //模拟评论
      
      token: 'your_token_here' // 添加 token 属性
    };
  },
  computed: {
    //计算属性
    notebookId() {
      return this.$route.params.notebookId; //从路由参数获取笔记本ID
    },
    noteId() {
      return this.$route.params.noteId; //// 从路由参数获取笔记ID
    }
  },
  methods: {
    //选择笔记
    selectNote(note) {
      this.selectedNote = note;
      this.$router.push({ name: 'NoteDetail', params: { notebookId: this.notebookId, noteId: note.noteId } });
    },
    prevPage() {
      // 实现上一页功能
    },
    nextPage() {
      // 实现下一页功能
    },
    //显示创建笔记模态框
    showCreateNoteModal() {
      this.isCreateNoteModalVisible = true;
    },
    //隐藏创建笔记模态框
    hideCreateNoteModal() {
      this.isCreateNoteModalVisible = false;
    },
    //清除搜索查询
    clearSearch() {
      this.searchQuery = ''; // 将 searchQuery 设置为空字符串
    },
    searchNotes() {
      // 实现搜索功能
    },
    //路由导航方法
    goToStart() {
      this.$router.push({ name: 'Start' });
    },
    goToCommunity() {
      this.$router.push({ name: 'Community' });
    },
    backToStart() {
      this.$router.push({ name: 'Start' }); 
    },
    backToNotebook() {
      this.$router.push({ name: 'Notebook' }); 
    },
    //后端完成之前的测试方法，直接引用测试数据中的笔记内容
    fetchNoteDetail() {
  const note = this.notes.find(note => note.noteId === this.noteId);
  if (note) {
    this.noteType = note.type;
    if (this.noteType === 0) {
      // 如果是树状图类型，尝试解析 JSON 字符串
      try {
        this.noteContent = JSON.parse(note.content);
      } catch (error) {
        console.error('Failed to parse tree data:', error);
        this.noteContent = note.content; // 如果解析失败，保留原始字符串
      }
    } else {
      this.noteContent = note.content;
    }
  } else {
    this.noteType = 'text';
    this.noteContent = '笔记不存在';
  }
},
editNote() {
  if (this.selectedNote) {
    if (this.selectedNote.type === 1) {
      // 如果笔记类型为 0，跳转到编辑界面
      this.$router.push({ 
        name: 'NoteEdit', 
        params: { 
          notebookId: this.notebookId, 
          noteId: this.noteId 
        } 
      });
    } else if (this.selectedNote.type === 0) {
      // 如果笔记类型为 1，跳转到空白页面
      this.$router.push({ name: 'MindnoteEdit' });
    }
  }
},
    addComment() {
      // 添加评论功能（暂不实现）
    },
    async fetchComments() {
      // try {
      //   const response = await axios.get(`/api/notes/${this.noteId}/comments`, {
      //     headers: {
      //       'Authorization': `Bearer ${this.token}`
      //     }
      //   });
      //   if (response.data.code === 200) {
      //     this.comments = response.data.data;
      //   } else {
      //     console.error(response.data.message);
      //   }
      // } catch (error) {
      //   console.error('Failed to fetch comments:', error);
      // }
    }
  },
  mounted() {
    // this.fetchNoteDetail();
  },
  watch: {
    '$route.params.noteId': 'fetchNoteDetail'
  }
};
</script>
  
<style>
.container {
  display: flex;
  height: 100vh;
}

/* 左侧侧边栏样式 */
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
.notebook-button {
  background-color: #4CAF50;
  color: white;
  border-radius: 20px;
  padding: 10px 20px;
  margin: 10px 20px;
  transition: background-color 0.3s;
}

.notebook-button:hover {
  background-color: #45a049;
}


.note-count-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.note-count {
  flex: 1;
  text-align: center;
}

.add-note {
  cursor: pointer;
  font-size: 1.2em;
  color: #000000; /* 可根据需要修改颜色 */
}

.icon {
    width: 50px;
    height: 50px;
    background-color: #b0b0b0;
    margin-bottom: 20px;
  }

  .icon-image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 确保图片填满圆形且不变形 */
}

.sidebar-button {
  margin: 10px 0;
  padding: 10px;
  width: 100%;
  background-color: #d0d0d0;
  border: none;
  text-align: center;
  cursor: pointer;
}

.sidebar-button:hover {
  background-color: #c0c0c0;
}

/* 中间笔记本内容区样式 */
.middle-section {
  width: 200px;
  background-color: #f0f0f0;
  padding: 10px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #000000;
}

.notebook-header {
  text-align: center;
  margin-bottom: 20px;
}

.search-container {
  position: relative;
  display: flex;
  align-items: center;
  border: 1px solid #ccc;
  padding: 5px;
  background-color: #fff;
}

.search-container input {
  border: none;
  outline: none;
  flex-grow: 1;
  padding-left: 25px; /* 为左侧图标留出空间 */
  padding-right: 2px; /* 为右侧图标留出空间 */
}

.search-icon {
  position: absolute;
  left: 10px;
  cursor: pointer;
}

.clear-icon {
  position: absolute;
  right: 10px;
  cursor: pointer;
}

.action-button {
  margin: 10px 0;
  padding: 10px;
  background-color: #d0d0d0;
  border: none;
  text-align: center;
  cursor: pointer;
}

.action-button:hover {
  background-color: #c0c0c0;
}

.notebook-list {
  flex-grow: 1;
  overflow-y: auto;
}

.note-item {
  padding: 30px;
  cursor: pointer;
}

.note-item.selected {
  background-color: #a0e0a0;
}

.pagination {
  display: flex;
  justify-content: space-between;
}

/* 右侧笔记详情区样式 */
.right-section {
  flex-grow: 1;
  padding: 20px;
  overflow-y: auto; /* 添加此行使右侧内容可滚动 */
}

.note-summary {
  margin-bottom: 20px;
}

.note-content {
  margin-bottom: 20px;
}

.edit-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
}

.edit-button:hover {
  background-color: #0056b3;
}

.summary-content {
  border: 1px solid #d0d0d0;
  padding: 10px;
  height: 100px;
  background-color: #f9f9f9;
}

.content-area {
  border: 1px solid #d0d0d0;
  padding: 10px;
  height: 420px;
  background-color: #f9f9f9;
}

.add-comment-button {
  margin-top: 20px;
  background-color: #28a745;
  color: white;
  border: none;
  padding: 10px 15px;
  cursor: pointer;
}

.add-comment-button:hover {
  background-color: #218838;
}

.comment-list {
  margin-top: 20px;
  border: 1px solid #d0d0d0;
  padding: 10px;
  background-color: #f9f9f9;
  max-height: 200px; /* 设置最大高度 */
  overflow-y: auto; /* 添加垂直滚动条 */
}

.comment-item {
  margin-bottom: 10px;
}

.comment-item strong {
  display: block;
  margin-bottom: 5px;
}
</style>

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
