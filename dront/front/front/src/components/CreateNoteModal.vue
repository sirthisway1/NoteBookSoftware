<!-- 已测试完成 -->
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
            <option v-for="notebook in notebooks" :key="notebook.notebookId" :value="notebook.notebookId">
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
import axios from 'axios';

export default {
  props: {
    visible: Boolean,
    theNoteType: Number,
  },
  data() {
    return {
      notebooks: [],
      selectedNotebook: '',
      noteName: '',
    };
  },
  methods: {
    async fetchNotebooks() {
      try {
        const response = await axios.get('/api/notebooks', {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
        if (response.data.code === 200) {
          this.notebooks = response.data.data;
        } else {
          console.error('获取笔记本列表失败:', response.data.message);
        }
      } catch (error) {
        console.error('获取笔记本列表时出错:', error);
      }
    },
    closeModal() {
      this.$emit('close');
    },
    async confirm() {
  if (!this.selectedNotebook || !this.noteName) {
    alert('请选择笔记本并输入笔记名称');
    return;
  }

  let content = null;
  if (this.theNoteType === 1) {
    content = JSON.stringify({
      "ur_im": "空", //紧急且重要
      "nur_im": "空",//不紧急但重要
      "ur_nim": "空",//紧急但不重要
      "nur_nim": "空",//不紧急且不重要
    });
  } else if (this.theNoteType === 2) {
    content = JSON.stringify({
      "advantage": "空", //优势
      "disadvantage": "空", //劣势
      "chance": "空", //机会
      "threat": "空",//威胁
    });
  } else if (this.theNoteType === 3) {
    content = JSON.stringify({
      "what": "空",
      "why": "空",
      "who": "空",
      "where": "空",
      "when": "空",
      "how": "空"
    });
  }

  try {
    const response = await axios.post('/api/notes', {
      notebookId: this.selectedNotebook,
      title: this.noteName,
      type: this.theNoteType,
      content: content // 使用默认的内容对象
    }, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    });

    if (response.data.code === 200) {
      console.log('笔记创建成功');
      this.$emit('note-created', response.data.data);
      this.closeModal();
    } else {
      console.error('创建笔记失败:', response.data.message);
    }
  } catch (error) {
    console.error('创建笔记时出错:', error);
  }
}

  },
  mounted() {
    this.fetchNotebooks();
  },
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
