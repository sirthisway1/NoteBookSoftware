<template>
    <div class="container">
      <div class="sidebar">
        <div class="sidebar-item" id="username">{{ username }}</div>
        <div class="sidebar-item" @click="goToStart">开始</div>
        <div class="sidebar-item notebook-button" @click="goToNotebook">
          <div class="icon-placeholder"><img src="/vue图片/图片2.png" alt="开始图标" class="icon-image"></div>
        <span>笔记本</span>
        </div>
        <div class="sidebar-item" @click="goToCommunity">发现社区</div>
        <!-- <div class="sidebar-item" @click="goToCommunity">标签管理</div> -->
      </div>
      <div class="main-content">
        <div class="notebooks">
          <div v-for="notebook in paginatedNotebooks" :key="notebook.notebookId" class="notebook-wrapper">
            <div class="notebook">
              <div class="notebook-header">
                <div class="notebook-name" @click="goToNotebookDetails(notebook)">{{ notebook.name }}</div>
                <div class="dropdown">
                  <button class="dropbtn" @click.stop="toggleDropdown(notebook.notebookId)">⚙️</button>
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
  

//假数据，测试用
export default {
  data() {
    return {
      username: '这里填写用户名',
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
    goToNotebookDetails(notebook) {
      const notebookPath = `/notebook/${notebook.notebookId}`;
      this.$router.push({ path: notebookPath });
    },
    async editNotebookName(notebookId) { //修改笔记本名称
    const newTitle = prompt("请输入新的笔记本名称：");
    if (newTitle) {
      try {
        const response = await fetch(`/api/notebooks/${notebookId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ title: newTitle })
        });
        const result = await response.json();
      if (result.code === 200) {
        if(1){
          alert("笔记本名称修改成功");
          this.notebooks = this.notebooks.map(notebook => 
            notebook.notebookId === notebookId ? { ...notebook, name: newTitle } : notebook  
        );
      } 
      else if (result.code === 501) {
          alert("token验证失败");
        } else if (result.code === 405) {
          alert("请求体不合法");
        } else if (result.code === 404) {
          alert("笔记本不存在");
        }
       }
      }
        catch (error) {
       console.error('Error editing notebook name:', error);
      }
    }
  },

  async editNotebookSummary(notebookId) { //修改笔记本简介
    const newSummary = prompt("请输入新的笔记本简介：");
    if (newSummary) {
      try {
        const response = await fetch(`/api/notebooks/${notebookId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ summary: newSummary })
        });
        const result = await response.json();
        if (result.code === 200) {
          alert("笔记本简介修改成功");
          this.notebooks = this.notebooks.map(notebook => 
            notebook.notebookId === notebookId ? { ...notebook, summary: newSummary } : notebook
          );
        } else if (result.code === 501) {
          alert("token验证失败");
        } else if (result.code === 405) {
          alert("请求体不合法");
        } else if (result.code === 404) {
          alert("笔记本不存在");
        }
      } catch (error) {
        console.error('Error editing notebook summary:', error);
      }
    }
  },
  async deleteNotebook(notebookId) {
    if (confirm("确定要删除这个笔记本吗？")) { //删除笔记本
      try {
        const response = await fetch(`/api/notebooks/${notebookId}`, {
          method: 'DELETE'
        });
        const result = await response.json();
        if (result.code === 200) {
          alert("笔记本删除成功");
          this.notebooks = this.notebooks.filter(notebook => notebook.notebookId !== notebookId);
          this.updateTotalPages(); // 更新总页数
        } else if (result.code === 501) {
          alert("token验证失败");
        } else if (result.code === 405) {
          alert("请求体不合法");
        } else if (result.code === 404) {
          alert("笔记本不存在");
        }
      } catch (error) {
        console.error('Error deleting notebook:', error);
      }
    }
  },
    async fetchNotebooks() {
      try {
        // 模拟延迟
        await new Promise(resolve => setTimeout(resolve, 500));
        // 模拟获取的笔记本ID列表
        const notebookIds = [1, 2, 3, 4, 5, 6];
        this.notebooks = await Promise.all(notebookIds.map(notebookId => this.fetchNotebookDetails(notebookId)));
        this.updateTotalPages(); // 更新总页数
      } catch (error) {
        console.error('Error fetching notebook IDs:', error);
      }
    },
    async fetchNotebookDetails(notebookId) {
      try {
        // 模拟延迟
        await new Promise(resolve => setTimeout(resolve, 200));
        // 模拟获取的笔记本详细信息
        const mockNotebookDetails = {
          1: {notebookId: 1, name: 'Notebook 1', summary: 'This is the first notebook', lastModified: '2023-06-01T12:00:00Z' },
          2: { notebookId: 2, name: 'Notebook 2', summary: 'This is the second notebook', lastModified: '2023-06-02T12:00:00Z' },
          3: { notebookId: 3, name: 'Notebook 3', summary: 'This is the third notebook', lastModified: '2023-06-03T12:00:00Z' },
          4: { notebookId: 4, name: 'Notebook 4', summary: 'This is the fourth notebook', lastModified: '2023-06-04T12:00:00Z' },
          5: {notebookId: 5, name: 'Notebook 5', summary: 'This is the fifth notebook', lastModified: '2023-06-05T12:00:00Z' },
          6: { notebookId: 6, name: 'Notebook 6', summary: 'This is the sixth notebook', lastModified: '2023-06-06T12:00:00Z' },
        };
        const notebookDetails = mockNotebookDetails[notebookId];
        return notebookDetails;
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
  },
  async mounted() {
    await this.fetchNotebooks();
  }
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

  
  .main-content {
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
    overflow: hidden; /* 防止内容滚动 */
  }
  
  .notebooks {
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-rows: 1fr 1fr;
    gap: 20px;
    flex-grow: 1;
  }
  
  .notebook-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .notebook {
    width: 90%;
    height: 90%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 10px;
    background-color: #f0f0f0;
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    position: relative;
  }
  
  .notebook-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .notebook-name {
    flex: 1;
    font-size: 1.5em;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    cursor: pointer;
  }
  
  .separator {
    width: 100%;
    border: none;
    border-top: 1px solid #ccc;
  }
  
  .notebook-description {
    flex: 1;
    font-size: 1em;
    text-align: left;
    padding-left: 10px;
  }
  
  .dropdown {
    position: relative;
    display: inline-block;
  }
  
  .dropbtn {
    background-color: #696969;
    color: white;
    padding: 10px;
    border: none;
    border-radius: 50%;
    cursor: pointer;
    font-size: 1em;
  }
  
  .dropdown-content {
    display: block;
    position: absolute;
    right: 0;
    background-color: #f9f9f9;
    min-width: 160px;
    box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
    z-index: 1;
  }
  
  .dropdown-item {
    padding: 12px 16px;
    cursor: pointer;
  }
  
  .dropdown-item:hover {
    background-color: #f1f1f1;
  }
  
  .pagination {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
  
  .pagination button {
    margin: 0 5px;
    padding: 10px;
    cursor: pointer;
  }
  </style>
  
 