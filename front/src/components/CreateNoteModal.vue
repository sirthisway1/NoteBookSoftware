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
          <el-select
            v-model="selectedNotebook"
            placeholder="选择一个笔记本"
            style="width: 100%;"
          >
            <el-option
              v-for="notebook in notebooks"
              :key="notebook.notebookId"
              :value="notebook.notebookId"
              :label="notebook.name"
            >
            </el-option>
          </el-select>
        </div>
        <div class="form-group">
          <label>输入笔记名称:</label>
          <el-input
            v-model="noteName"
            placeholder="请输入笔记名称"
            clearable
            style="width: 100%;"
          ></el-input>
        </div>
        
        <div class="form-group">
          <label>输入标签:</label>
          <el-input
            v-model="tagsInput"
            placeholder="请输入标签"
            clearable
            @keydown.enter.native="addTag"
            style="width: 100%;"
          ></el-input>
        </div>
      </div>
      <div class="modal-footer">
        <div class="mb-4"><el-button  @click="confirm" type="primary" plain>确认</el-button></div>
      </div>
    </div>
  
  </div>
</template>

<script>
import axios from 'axios';
import { ElMessage } from 'element-plus'


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
      tags:[]
    };
  },
  computed:{
      tagsInput: {
      get() {
        return this.tags.join(',');
      },
      set(newValue) {
        this.tags = newValue.split(',').map(tag => tag.trim()).filter(tag => tag !== '');
      }
    }
  },
  methods: {
    addTag(event) {
    event.preventDefault();
    const newTag = event.target.value.trim();
    if (newTag && !this.tags.includes(newTag)) {
      this.tags.push(newTag);
    }
    event.target.value = '';
  },
    async fetchNotebooks() {
      try {
        const token=localStorage.getItem('token');
        const response = await axios.get('/api/notebooks/getAllNotebooks', {
          headers: {token: token}
        });
        if (response.data.code === "200") {
          this.notebooks = response.data.data.notebooks;
        }
      } catch (error) {
        console.error('获取笔记本列表时出错:', error);
        if (error.response) {
            alert('获取笔记本列表失败: ' + error.response.data.message);
        }
      }
    },
    closeModal() {
      this.$emit('close');
    },
    async confirm() {
      const token=localStorage.getItem('token');
      if (!this.selectedNotebook || !this.noteName) {
        alert('请选择笔记本并输入笔记名称');
        return;
      }

      //李子禹新增部分
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
      //到此结束


      try {
        const response = await axios.post('/api/notes/create', {
          notebookId: this.selectedNotebook,
          title: this.noteName,
          content: content,  // 默认的content为null
          tags:this.tags,
          type: this.theNoteType,
        }, {
          headers: {token: token}
        });

        if (response.data.code === "200") {
          ElMessage({
            duration:1000,
            message: '笔记创建成功',
            type: 'success',
          })
          this.$emit('note-created', response.data.data);
          
          this.$emit('noteCreated');
          setTimeout(() => {
            this.closeModal();
          }, 1500);  // 1.5秒后关闭模态框
          
        }else{
          console.error('创建笔记时出错:', response.data);
        }
      } catch (error) {
        console.error('创建笔记时出错:', error);
        if (error.response) {
            alert('创建笔记失败: ' + error.response.data.message);
        }
      }
    },
    
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
