<template>
  <div class="container">
    <!-- 左侧侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-user">
          <img :src="useravatar || 'default-avatar.png'" alt="User Avatar" class="sidebar-avatar">
          <div class="sidebar-username" id="username">{{ username }}</div>
      </div>
      <div class="sidebar-item" @click="goToStart">开始</div>
      <div class="sidebar-item active" @click="goToNotebook">
        <el-icon :size="40"><Management /></el-icon>
       笔记本
      </div>
      <div class="sidebar-item" @click="goToCommunity">发现社区</div>
      <div class="sidebar-item" @click="goToUserCenter">用户中心</div>
      <!-- <div class="sidebar-item" @click="goToCommunity">标签管理</div> -->
    </div>
    
    <!-- 中间笔记本内容区 -->
    <div class="middle-section">
      <div class="note-header">
    <h2>{{ notebookName }}</h2>
    <div class="note-count-container">
      <p class="note-count">{{ notes.length }}条笔记</p>
      <i class="fas fa-plus add-note" @click="showCreateNoteModal"></i>
    </div>
  </div>
      <!-- 操作按钮 -->
      <button class="action-button" @click="backToStart">返回首页</button>
      <button class="action-button" @click="backToNotebook">目录</button>

      <!-- 笔记列表 -->
      <!-- 笔记列表部分 -->
      <div class="notebook-list">
        <div v-for="(note, index) in notes" :key="index" class="note-item" :class="{ selected: selectedNote && selectedNote.noteId === note.noteId }" @click="selectNote(note)">
          {{ note.title }}
        </div>
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
          <button v-if="selectedNote" class="edit-button" @click="analysisKeyword">关键词分析</button>
        </div>
        <div class="content-area">
          <template v-if="noteType === 0">
            <div v-html="noteContent"></div>
          </template>
          <template v-else>
            <EChartsComponent :option="chartOption" v-if="chartOption" />
          </template>
        </div>
      </div>
      <div class="keyward-summary">
          <h3>关键字显示</h3>
          <div class="keyword-content">
            {{ keywardSummery }}
          </div>
      </div>

    </div>
    
    <!-- 包含模态框组件 -->
    <CreateNoteModal :visible="isCreateNoteModalVisible" @close="hideCreateNoteModal" />
  </div>
</template>

  
  
  <script>
 
 import axios from 'axios';

import CreateNoteModal from './CreateNoteModal.vue';
// import NoteCreateTree from './NoteCreateTree.vue';
import EChartsComponent from './EChartsComponent.vue';

import { ElMessage } from 'element-plus'

export default {
  components: {
    CreateNoteModal,
    // NoteCreateTree,
    EChartsComponent,
  },
  data() {
    return {
      useravatar:'',
      notes:[],
      selectedNote: null,
      hasMoreNotes: false, // 设置翻页功能的状态
      searchQuery: '', // 添加 searchQuery 属性
      notebookSummary: '这里是简介',
      notebookName: '这里是笔记本名',
      isCreateNoteModalVisible: false,
      chartOption: null,
      noteContent: '',
      isLoading: true,
      noteType: 0,
      notebookId: this.$route.params.notebookId, // 从路由获取笔记本ID
      keywardSummery: '这里是提取出来的关键字'
      //模拟评论
      
      
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
    goToUserCenter() {
      this.$router.push({ name: 'UserCenter' });
    },
    backToStart() {
      this.$router.push({ name: 'Start' }); 
    },
    backToNotebook() {
      this.$router.push({ name: 'Notebook' }); 
    },
    //后端完成之前的测试方法，直接引用测试数据中的笔记内容
    async fetchNoteDetail() {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('请先登录');
        return;
      }
      try {
        const response = await axios.get(`/api/notes/detail/${this.noteId}`, {headers: { token: token } });
        if (response.data.code === "200") {
          const  note  = response.data.data;
          // this.notebookSummary = summary;
          this.selectedNote = note;
          this.noteType = note.type;
          if (this.noteType === 1 || this.noteType === 2 || this.noteType === 3) {
            this.noteContent = JSON.parse(note.content);
            this.setChartOption(note.type, this.noteContent);
          } else {
            this.noteContent = note.content;
            this.chartOption = null;
          }
        } else {
          console.error('获取笔记详情失败:', response.data.message);
        }
      } catch (error) {
        console.error('获取笔记详情时出错:', error);
      }
    },


    //获取笔记本详细+笔记详细
    async fetchNotebookDetails() {
      const token = localStorage.getItem('token');
      if (!token) {
        alert('请先登录');
        return;
      }
      
      this.isLoading = true;
      try {
        // 获取笔记本详情
        const notebookResponse = await axios.get(`/api/notebooks/${this.notebookId}`, {
          headers: { token: token }
        });
        
        if (notebookResponse.data.code === "200") {
          this.notebookName = notebookResponse.data.data.notebookDetail.name;
          this.notebookSummary = notebookResponse.data.data.notebookDetail.summary;
          
          // 笔记ID集合在 notebookResponse.data.data.noteIds 中
          const noteIds = notebookResponse.data.data.notebookDetail.noteId;
          
          // 获取每个笔记的详细信息
          const notePromises = noteIds.map(noteId => 
            axios.get(`/api/notes/detail/${noteId}`, {
              headers: { token: token }
            })
          );
          
          const noteResponses = await Promise.all(notePromises);
          
          // 处理每个笔记的响应
          this.notes = noteResponses.map(response => {
            if (response.data.code === "200") {
              return response.data.data;
            }
            return null;
          }).filter(note => note !== null);
        } else {
          console.error('获取笔记本详情失败:', notebookResponse.data.message);
        }
      } catch (error) {
        console.error('获取笔记本详情时出错:', error);
      } finally {
        this.isLoading = false;
      }
    },

  // 设置图表选项的方法
  setChartOption(type, content) {
    // 定义格式化函数，用于设置富文本标签的格式
    const getRichLabelFormatter = (title, text) => {
      return `{title|${title}}\n\n{text|${text}}`;
    };

    // 定义富文本样式
    const richText = {
      title: {
        fontWeight: 'bold', // 加粗字体
        fontSize: 14, // 设置字体大小
        color: '#000', // 设置字体颜色
      },
      text: {
        fontSize: 12, // 设置字体大小
        color: '#333', // 设置字体颜色
      },
    };

    // 根据笔记类型设置不同的图表选项
    if (type === 1) {
      // 如果类型是时间管理四象限
      this.chartOption = {
        title: {
          text: '时间管理四象限', // 图表标题
          left: 'center', // 标题位置
        },
        xAxis: {
          type: 'value', // 数值轴
          min: -1, // 最小值
          max: 1, // 最大值
          show: true, // 隐藏坐标轴
        },
        yAxis: {
          type: 'value', // 数值轴
          min: -1, // 最小值
          max: 1, // 最大值
          show: true, // 隐藏坐标轴
        },
        series: [{
          type: 'scatter', // 使用散点图
          label: {
            show: true, // 显示标签
            position: 'inside', // 标签位置
            align: 'center', // 标签水平对齐
            verticalAlign: 'middle', // 标签垂直对齐
            formatter: params => {
              // 根据数据点的位置返回不同的标签内容
              const data = params.value;
              if (data[0] === 0.5 && data[1] === 0.5) return getRichLabelFormatter('紧急且重要', content.ur_im);
              if (data[0] === -0.5 && data[1] === 0.5) return getRichLabelFormatter('不紧急但重要', content.nur_im);
              if (data[0] === 0.5 && data[1] === -0.5) return getRichLabelFormatter('紧急但不重要', content.ur_nim);
              if (data[0] === -0.5 && data[1] === -0.5) return getRichLabelFormatter('不紧急且不重要', content.nur_nim);
            },
            rich: richText, // 使用富文本样式
          },
          data: [
            { value: [0.5, 0.5] },
            { value: [-0.5, 0.5] },
            { value: [0.5, -0.5] },
            { value: [-0.5, -0.5] },
          ],
        }],
      };
    } else if (type === 2) {
      // 如果类型是SWOT分析
      this.chartOption = {
        title: {
          text: 'SWOT', // 图表标题
          left: 'center', // 标题位置
        },
        xAxis: {
          show: false, // 隐藏坐标轴
        },
        yAxis: {
          show: false, // 隐藏坐标轴
        },
        series: [{
          type: 'sunburst', // 使用旭日图
          label: {
            show: true, // 显示标签
            formatter: params => {
              // 根据数据点的名称返回不同的标签内容
              const name = params.name;
              if (name === '优势') return getRichLabelFormatter('优势', content.advantage);
              if (name === '劣势') return getRichLabelFormatter('劣势', content.disadvantage);
              if (name === '机会') return getRichLabelFormatter('机会', content.chance);
              if (name === '威胁') return getRichLabelFormatter('威胁', content.threat);
            },
            rich: richText, // 使用富文本样式
          },
          data: [
            { name: '优势', value: 1 },
            { name: '劣势', value: 1 },
            { name: '机会', value: 1 },
            { name: '威胁', value: 1 },
          ],
          radius: [0, '90%'], // 设置旭日图的半径范围
        }],
      };
    } else if (type === 3) {
      // 如果类型是六问法 (5W1H)
      this.chartOption = {
        title: {
          text: '5W1H', // 图表标题
          left: 'center', // 标题位置
        },
        xAxis: {
          show: false, // 隐藏坐标轴
        },
        yAxis: {
          show: false, // 隐藏坐标轴
        },
        series: [{
          type: 'sunburst', // 使用旭日图
          label: {
            show: true, // 显示标签
            formatter: params => {
              // 根据数据点的名称返回不同的标签内容
              const name = params.name;
              if (name === 'What') return getRichLabelFormatter('What', content.what);
              if (name === 'Why') return getRichLabelFormatter('Why', content.why);
              if (name === 'Who') return getRichLabelFormatter('Who', content.who);
              if (name === 'Where') return getRichLabelFormatter('Where', content.where);
              if (name === 'When') return getRichLabelFormatter('When', content.when);
              if (name === 'How') return getRichLabelFormatter('How', content.how);
            },
            rich: richText, // 使用富文本样式
          },
          data: [
            { name: 'What', value: 1 },
            { name: 'Why', value: 1 },
            { name: 'Who', value: 1 },
            { name: 'Where', value: 1 },
            { name: 'When', value: 1 },
            { name: 'How', value: 1 },
          ],
          radius: [0, '90%'], // 设置旭日图的半径范围
        }],
      };
    } else {
      // 如果不是图表类型，清除图表选项
      this.chartOption = null;
    }
  },

  
  editNote() {
    if (this.selectedNote) {
      this.$router.push({
        name: 'NoteEdit',
        params: {
          notebookId: this.notebookId,
          noteId: this.selectedNote.noteId,
          noteType: this.selectedNote.type,
          noteContent: this.selectedNote.content,
          noteName: this.selectedNote.title,
          isPrivate: this.selectedNote.isPrivate,
          tags: this.selectedNote.tags, // 传递标签
        }
      });
    }
  },
    
    //获取当前用户
    async fetchCurrentUser() {
      const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          this.$router.push('/login');
          return;
        }

      try {
        const response = await axios.get('/api/user',{headers: { token: token }});
        if (response.data && response.data.code === "200") {
          this.currentUser = response.data.data;
          this.username = this.currentUser.username;
          this.useravatar = this.currentUser.avatar;
          console.log('用户名：',this.username);
        }
      } catch (error) {
        console.error("Error fetching current user:", error);
      }
    },

    async analysisKeyword() {
      if (!this.selectedNote || !this.selectedNote.content) {
        console.error('No note selected or note content is empty');
        return;
      }

      const token = localStorage.getItem('token');
      if (!token) {
        alert('请先登录');
        return;
      }

      try {
        const response = await axios.post('/api/model/keyWord', 
          null,
          { params: {
              text: this.selectedNote.content
            },
            headers: { 
              'Content-Type': 'application/x-www-form-urlencoded',
              token: token 
            },
          }
        );

        if (response.data && response.data.code === "200") {
          ElMessage({
            showClose: true,
            message: '分析成功',
            type: 'success',
          })
          const keywords = response.data.data.keywords;
          this.keywardSummery = keywords.join(', '); // 将关键词数组转换为字符串
        } else {
          console.error('关键词分析失败:', response.data.message);
          this.keywardSummery = '关键词分析失败';
        }
      } catch (error) {
        console.error('关键词分析出错:', error);
        this.keywardSummery = '关键词分析出错';
      }
    }
  },
  mounted() {
  this.fetchNoteDetail();
  this.fetchNotebookDetails();
  this.fetchCurrentUser();
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

.sidebar-item.active{
  background-color: #409bf6;
  color: white;
}
/* 用户名以及头像 */
.sidebar-user {
  display: flex;
  align-items: center;
  padding: 15px;
  margin-bottom: 20px;
  position: relative; /* 添加这行 */
  left:40px;
}

.sidebar-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
  flex-shrink: 0; /* 防止头像被压缩 */
  position: relative; /* 添加这行 */
  top: 0px; /* 调整这个值来控制上移的距离 */
}

.sidebar-username {
  font-weight: bold;
  flex-grow: 1;
  text-align: left;
  display: flex;
  align-items: center; /* 垂直居中文本 */
  min-height: 40px; /* 与头像高度一致 */
}
/* 用户名以及头像 */



.note-count-container {
  display: flex;
  align-items: center; /* 垂直居中对齐 */
  position: relative; /* 为了让子元素绝对定位 */
  width: 100%;
}

.note-count {
  position: absolute; /* 绝对定位 */
  left: 50%; /* 距离左边 50% */
  transform: translateX(-50%); /* 左右居中对齐 */
  text-align: center;
}

.add-note {
  margin-left: auto; /* 将加号推到最右边 */
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
  background-color: #f0f0f0;
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
  background-color: #489ccd;
  border: none;
  text-align: center;
  cursor: pointer;
}

.action-button:hover {
  background-color: #73c7ff;
}

.notebook-list {
  flex-grow: 1;
  overflow-y: auto;
  background-color: #f0f0f0;
}

.note-item {
  padding: 30px;
  cursor: pointer;
}

.note-item.selected {
  background-color: #409bf6;
}

.note-header {
  display: flex;
  flex-direction: column; /* 垂直排列 */
  align-items: center; /* 水平居中对齐 */
  margin-bottom: 20px; /* 调整与下方内容的距离 */
  text-align: center; /* 使文本居中 */
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
  background-color: #28a745;
  color: white;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
  margin-left: auto; /* 添加这行 */
}

.edit-button:hover {
  background-color: #006527;
}

.summary-content {
  border: 1px solid #d0d0d0;
  padding: 10px;
  height: 100px;
  background-color: #f9f9f9;
}

.content-header {
  display: flex; /* 添加这行 */
  justify-content: space-between; /* 可选：如果有多个子元素可以均匀分布 */
  align-items: center; /* 垂直居中 */
}


.content-area {
  border: 1px solid #d0d0d0;
  padding: 10px;
  height: 400px;
  background-color: #f9f9f9;
  position: relative;
}

.content-area > div {
  width: 100%;
  height: 100%;
}

.keyword-content{
  border: 1px solid #d0d0d0;
  padding: 10px;
  height: 200px;
  background-color: #f9f9f9;
  position: relative;
}





.quadrant, .swot, .five-wh {
  display: grid;
  gap: 20px;
}

.quadrant {
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
}

.swot {
  grid-template-columns: 1fr 1fr;
  grid-template-rows: 1fr 1fr;
}

.five-wh {
  grid-template-columns: 1fr 1fr 1fr;
  grid-template-rows: 1fr 1fr;
}

.quadrant-item, .swot-item, .five-wh-item {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

textarea {
  width: 100%;
  height: 100px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  resize: none;
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
