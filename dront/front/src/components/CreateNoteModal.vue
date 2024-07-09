<template>
  <div v-if="visible" class="modal-overlay">
    <div class="modal-content">
      <div class="modal-header">
        <h3>新建笔记</h3>
        <button class="close-button" @click="closeModal">✖</button>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <label>请选择笔记本:</label>
          <select v-model="selectedNotebook">
            <option v-for="notebook in notebooks" :key="notebook.notebookId" :value="notebook.name">
              {{ notebook.name }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>输入笔记名称:</label>
          <input type="text" v-model="noteName">
        </div>
      </div>
      <div class="modal-footer">
        <button class="confirm-button" @click="confirm">确认</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    visible: Boolean,
  },
  data() {
    return {
      //notebooks[];
      //下为测试数据
      notebooks:[
      { notebookId: 1, name: 'Notebook 1', summary: 'This is the first notebook', lastModified: '2023-06-01T12:00:00Z' },
      { notebookId: 2, name: 'Notebook 2', summary: 'This is the second notebook', lastModified: '2023-06-02T12:00:00Z' },
      { notebookId: 3, name: 'Notebook 3', summary: 'This is the third notebook', lastModified: '2023-06-03T12:00:00Z' },
      { notebookId: 4, name: 'Notebook 4', summary: 'This is the fourth notebook', lastModified: '2023-06-04T12:00:00Z' },
      { notebookId: 5, name: 'Notebook 5', summary: 'This is the fifth notebook', lastModified: '2023-06-05T12:00:00Z' },
      { notebookId: 6, name: 'Notebook 6', summary: 'This is the sixth notebook', lastModified: '2023-06-06T12:00:00Z' },
      ],
      selectedNotebook: '',
      noteName: '',
    };
  },
  methods: {
    // async fetchNotebooks() {
      // const token = localStorage.getItem('token');
      // if (!token) {
      //   alert('请先登录');
      //   return;
      // }

    //   try {
    //     const response = await fetch('/api/notebooks', {
    //       method: 'GET',
    //       headers: {
    //         'Authorization': `Bearer ${token}`
    //       }
    //     });

    //     const data = await response.json();
    //     if (data.code === 200) {
    //       const notebookIds = data.data.notebookIds;
    //       this.notebooks = await Promise.all(notebookIds.map(notebookId => this.fetchNotebookDetails(notebookId)));
    //     } else {
    //       alert('获取笔记本ID列表失败');
    //     }
    //   } catch (error) {
    //     console.error('获取笔记本ID列表时出错:', error);
    //   }
    // },
    // async fetchNotebookDetails(notebookId) {
    //   const token = localStorage.getItem('token');
    //   try {
    //     const response = await fetch(`/api/notebooks/${notebookId}`, {
    //       method: 'GET',
    //       headers: {
    //         'Authorization': `Bearer ${token}`
    //       }
    //     });

    //     const data = await response.json();
    //     if (data.code === 200) {
    //       return data.data;
    //     } else if (data.code === 405) {
    //       alert(`获取笔记本详细信息失败: ${data.message}`);
    //       return null;
    //     } else {
    //       alert(`获取笔记本详细信息失败: ${data.message}`);
    //       return null;
    //     }
    //   } catch (error) {
    //     console.error(`获取ID为 ${notebookId} 的笔记本详情时出错:`, error);
    //     return null;
    //   }
    // },
    closeModal() {
      this.$emit('close');
    },
    confirm() {
    console.log('选择的笔记本:', this.selectedNotebook);
    console.log('笔记名称:', this.noteName);

    const notebook = this.notebooks.find(n => n.name === this.selectedNotebook);
    if (notebook) {
      this.$emit('close');
      this.$router.push({ name: 'NoteCreate', params: { notebookId: notebook.notebookId, noteName: this.noteName } });
    }
  }


  },
  //正式运行时，必须重新调用这个钩子
  // mounted() {
  //   this.fetchNotebooks();
  // },
};
</script>

<style>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  max-width: 100%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.modal-body {
  margin-top: 10px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group select,
.form-group input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.modal-footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.confirm-button {
  background-color: #4CAF50;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.confirm-button:hover {
  background-color: #45a049;
}

.close-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}
</style>
