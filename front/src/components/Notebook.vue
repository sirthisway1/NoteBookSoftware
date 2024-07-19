<!-- 已测试接口 -->
<template>
  <div class="container">
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
    <div class="main-content">
      <div class="notebooks">
        <div v-for="notebook in paginatedNotebooks" :key="notebook.notebookId" class="notebook-wrapper">
          <div class="notebook">
            <div class="notebook-header">
              <div class="notebook-name" @click="goToNotebookDetails(notebook)">{{ notebook.name }}</div>
              <div class="dropdown">
                <!-- <button class="dropbtn" @click.stop="toggleDropdown(notebook.notebookId)"></button> -->
                <el-icon class="dropbtn" @click.stop="toggleDropdown(notebook.notebookId)"><Tools /></el-icon>
                <div class="dropdown-content" v-if="notebook.showDropdown">
                  <div class="dropdown-item" @click="editNotebookName(notebook.notebookId)">修改笔记本名称</div>
                  <div class="dropdown-item" @click="editNotebookSummary(notebook.notebookId)">修改笔记本简介</div>
                  <div class="dropdown-item" @click="deleteNotebook(notebook.notebookId)">删除笔记本</div>
                </div>
              </div>
            </div>
            <hr class="separator">
            <div class="notebook-description" @click="goToNotebookDetails(notebook)">{{ notebook.summary }}</div>
          </div>
        </div>
      </div>
      <div class="pagination" v-if="totalPages > 1">
        <button @click="prevPage" :disabled="currentPage === 1">上一页</button>
        <button v-for="page in totalPagesArray" :key="page" @click="goToPage(page)">{{ page }}</button>
        <button @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
      </div>
    </div>
  </div>
</template>



<script>
import axios from 'axios';
import { ElMessage } from 'element-plus'
export default {
data() {
  return {
    useravatar:'',
    username: '',
    notebookIds: [], //存储获取的笔记本ID
    notebooks: [], // 存储获取的笔记本详细信息
    currentPage: 1,
    totalPages: 1,
  };
},
computed: {
  sortedNotebooks() {
    return this.notebooks.sort((a, b) => new Date(b.lastModified) - new Date(a.lastModified));
  },
  paginatedNotebooks() {
    const notebooksPerPage = 4;
    const startIndex = (this.currentPage - 1) * notebooksPerPage;
    const endIndex = startIndex + notebooksPerPage;
    return this.sortedNotebooks.slice(startIndex, endIndex);
  },
  totalPagesArray() {
    return Array.from({ length: this.totalPages }, (v, k) => k + 1);
  }
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
  goToUserCenter() {
      this.$router.push({ name: 'UserCenter' });
    },
  goToNotebookDetails(notebook) {
    const notebookPath = `/notebook/${notebook.notebookId}`;
    this.$router.push({ path: notebookPath });

    // this.$router.push({
    //     name: 'NotebookDetail',
    //     params: {
    //       notebookId: notebook.notebookId,
          
    //     }
    //   });
  },
  //编辑笔记本名称
  async editNotebookName(notebookId) {

    const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          return;
        }

    const newTitle = prompt("请输入新的笔记本名称：");
    if (newTitle) {
      try {
        const response = await axios.put(`/api/notebooks/${notebookId}`, { name: newTitle },{headers: { token: token }});
        if (response.data.code === "200") {
          ElMessage({
            duration:1000,
            message: '笔记本名称修改成功',
            type: 'success',
          })
          this.notebooks = this.notebooks.map(notebook => 
            notebook.notebookId === notebookId ? { ...notebook, name: newTitle } : notebook
          );
        } else {
          console.error("修改失败");
        }
      } catch (error) {
        console.error('Error editing notebook name:', error);
        if (error.response) {
            alert('笔记本名称修改: ' + error.response.data.message);
        }
      }
    }
  },
  //编辑笔记本简介
  async editNotebookSummary(notebookId) {

    const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          return;
        }

    const newSummary = prompt("请输入新的笔记本简介：");
    if (newSummary) {
      try {
        const response = await axios.put(`/api/notebooks/${notebookId}`, { summary: newSummary },{headers: { token: token }});
        if (response.data.code === "200") {
          
          ElMessage({
            duration:1000,
            message: '笔记本简介修改成功',
            type: 'success',
          })
          this.notebooks = this.notebooks.map(notebook => 
            notebook.notebookId === notebookId ? { ...notebook, summary: newSummary } : notebook
          );
        } else {
          console.error("修改失败");
        }
      } catch (error) {
        console.error('Error editing notebook summary:', error);
        if (error.response) {
            alert('笔记本名称修改: ' + error.response.data.message);
        }
      }
    }
  },
  //删除笔记本
  async deleteNotebook(notebookId) {
    const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          return;
        }

    if (confirm("确定要删除这个笔记本吗？")) {
      try {
        const response = await axios.delete(`/api/notebooks/${notebookId}`,{headers: { token: token }});
        if (response.data.code === "200") {
          
          ElMessage({
            duration:1000,
            message: '笔记本删除成功',
            type: 'success',
          })
          this.notebooks = this.notebooks.filter(notebook => notebook.notebookId !== notebookId);
          this.updateTotalPages();
        } else {
          alert(response.data.message);
        }
      } catch (error) {
        console.error('Error deleting notebook:', error);
      }
    }
  },
  //获取所有笔记本ID
  async fetchNotebooksId() {
    const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          return;
        }

    console.log('Fetching notebooks...');
    try {
      const response = await axios.get('/api/notebooks/getAllId',{
          headers: { token: token }
        });
      // console.log('Response:', response.data);
      if (response.data.code === "200" && response.data.data.ids) {
        this.notebookIds = response.data.data.ids;
        console.log('Notebook IDs:', this.notebookIds);
      } else {
        console.error('Unexpected response structure:', response.data);
      }
    } catch (error) {
      console.error('Error fetching notebooks:', error);
    }
    },
  //获得单个笔记本详细信息
  async fetchNotebookDetails(notebookId) {
    const token = localStorage.getItem('token');
        if (!token) {
          alert('请先登录');
          return;
        }
    console.log(`Fetching details for notebook ID: ${notebookId}`);
    try {
      const response = await axios.get(`/api/notebooks/${notebookId}`,{
          headers: { token: token }
        });
      console.log('Response:', response.data);
      if (response.data.code === "200") {
          const notebookDetails = {
          notebookId: response.data.data.notebookDetail.notebookId, 
          name: response.data.data.notebookDetail.name,
          summary: response.data.data.notebookDetail.summary,
          lastModified: response.data.data.notebookDetail.lastModified
        };
        console.log(`Notebook details ${notebookId}:`, notebookDetails);
        return notebookDetails;
      } else {
          console.error(`Failed to fetch notebook details for ID ${notebookId}:`, response.data.message);
          return null;
        }
      } catch (error) {
        console.error(`Error fetching notebook details for ID ${notebookId}:`, error);
        return null;
      }
  },
  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  },
  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  },
  goToPage(page) {
    this.currentPage = page;
  },
  updateTotalPages() {
    const notebooksPerPage = 4;
    this.totalPages = Math.ceil(this.notebooks.length / notebooksPerPage);
  },
  toggleDropdown(notebookId) {
    const notebook = this.notebooks.find(nb => nb.notebookId === notebookId);
    notebook.showDropdown = !notebook.showDropdown;
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
},
async mounted() {
  await this.fetchCurrentUser();
  const token = localStorage.getItem('token');
  if (!token) {
    // 重定向到登录页面或显示错误信息
    return; 
  }

  await this.fetchNotebooksId();
  if (this.notebookIds && this.notebookIds.length > 0) {
    for (const notebookId of this.notebookIds) {
      const details = await this.fetchNotebookDetails(notebookId);
      if (details) {
        this.notebooks.push(details);
      }
    }
    this.updateTotalPages();
  } else {
    console.log('No notebook IDs found');
  }
},


};
</script>

<style scoped>
.container {
  display: flex;
  height: 100vh;
  overflow: hidden; /* 防止右边内容滚动 */
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
  left: 40px;
}

.sidebar-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
  border: 4px solid #409bf6;
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


.main-content {
  
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  padding: 30px;
  background-color: #f5f7fa;
  overflow: hidden;
}

.notebooks {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 25px;
  flex-grow: 1;
  height: calc(100% - 70px); /* 减去分页的高度 */
}

.notebook-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
}

.notebook {
  width: 100%;
  height: 80%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.notebook:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.notebook-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.notebook-name {
  flex: 1;
  font-size: 1.4em;
  font-weight: bold;
  color: #333;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.1);
}

.separator {
  width: 100%;
  border: none;
  border-top: 1px solid #e0e0e0;
  margin: 10px 0;
}

.notebook-description {
  flex: 1;
  font-size: 0.9em;
  line-height: 1.4;
  color: #666;
  padding-left: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  line-clamp: 3;
  -webkit-box-orient: vertical;
}

.dropbtn {
  background-color: #4c8caf;
  color: white;
  padding: 8px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 0.9em;
  transition: background-color 0.3s;
}

.dropbtn:hover {
  background-color: #77bdeb;
}

.dropdown-content {
  display: none;
  position: absolute;
  right: 0;
  background-color: #ffffff;
  min-width: 160px;
  box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
  z-index: 1;
  border-radius: 5px;
}

.dropdown:hover .dropdown-content {
  display: block;
}

.dropdown-item {
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.dropdown-item:hover {
  background-color: #f1f1f1;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  height: 50px; /* 固定分页的高度 */
}

.pagination button {
  margin: 0 5px;
  padding: 10px 15px;
  cursor: pointer;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  transition: background-color 0.3s;
}

.pagination button:hover {
  background-color: #45a049;
}
</style>

