<template>
    <div class="tree-chart">
      <div v-if="!readOnly" class="template-selector">
        <select  v-model="selectedTemplate" @change="changeTemplate">
          <option value="defaultTree1">SWOT分析</option>
          <option value="defaultTree2">5W1H分析</option>
          <option value="defaultTree3">时间管理四象限分析</option>
          <option value="defaultTree4">单一根节点</option>
        </select>
      </div>
      <div class="zoom-controls">
        <button @click="scale -= 0.1">缩小</button>
        <button @click="scale = 1">重置</button>
        <button @click="scale += 0.1">放大</button>
      </div>
      <div class="tree-container-wrapper">
        <div class="tree-container" :style="{ transform: `scale(${scale})` }">
          <TreeNode :model="currentTree" @on-add="add" @on-update="update" @on-remove="remove"/>
        </div>
      </div>
  
      <!--向后端发送树结构的数据-->
      <div v-if="!readOnly" class="save-button">
        <button @click="saveTree">保存树结构</button>
    </div>
    </div>
  </template>
  
  <script>
  import TreeNode from './TreeNode.vue'
  
  const defaultTree1 = {
    "resId": 0,
    "parentId": null,
    "resName": "SWOT分析",
    "extend": true,
    "children": [
      {"resId": 1, "parentId": 0, "resName": "优势", "extend": true, "children": []},
      {"resId": 2, "parentId": 0, "resName": "劣势", "extend": true, "children": []},
      {"resId": 3, "parentId": 0, "resName": "机会", "extend": true, "children": []},
      {"resId": 4, "parentId": 0, "resName": "威胁", "extend": true, "children": []}
    ]
  }
  
  const defaultTree2 = {
    "resId": 0,
    "parentId": null,
    "resName": "5W1H分析",
    "extend": true,
    "children": [
      {"resId": 1, "parentId": 0, "resName": "What (什么)", "extend": true, "children": []},
      {"resId": 2, "parentId": 0, "resName": "Why (为什么)", "extend": true, "children": []},
      {"resId": 3, "parentId": 0, "resName": "Who (谁)", "extend": true, "children": []},
      {"resId": 4, "parentId": 0, "resName": "Where (在哪里)", "extend": true, "children": []},
      {"resId": 5, "parentId": 0, "resName": "When (何时)", "extend": true, "children": []},
      {"resId": 6, "parentId": 0, "resName": "How (如何)", "extend": true, "children": []}
    ]
  }
  
  const defaultTree3 = {
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
  }
  
  const defaultTree4 = {
    "resId": 0,
    "parentId": null,
    "resName": "根节点",
    "extend": true,
    "children": []
  }
  
  export default {
    name: 'TreeChart',
    components: {
      TreeNode
    },
    props: {
      readOnly: {
        type: Boolean,
        default: false
      },
      initialTree: {
      type: Object,
      required: true
      }
    },
    data() {
      return {
        currentTree: JSON.parse(JSON.stringify(defaultTree4)), // 深拷贝 defaultTree4 作为默认模板
        scale: 1,
        selectedTemplate: 'defaultTree4',
        templates: {
          defaultTree1,
          defaultTree2,
          defaultTree3,
          defaultTree4
        }
      }
    },
    
    methods: {
      //切换模板函数
      changeTemplate() {
        // 切换模板时，直接使用选定模板的深拷贝
        this.currentTree = JSON.parse(JSON.stringify(this.templates[this.selectedTemplate]))
      },
      //新增节点函数
      add(node) {
        const newNode = {
          resId: Date.now(),
          parentId: node.resId,
          resName: '新节点',
          message: '',
          extend: true,
          children: []
        }
        if (!node.children) {
          this.$set(node, 'children', [])
        }
        node.children.push(newNode)
        this.$forceUpdate()
      },
      //更新结点函数
      update(node) {
        console.log('节点已更新:', node)
      },
      //删除结点函数
      remove(node) {
        if (node.resId === 0) {
          alert('根节点不能删除')
          return
        }
        const removeNode = (tree, nodeToRemove) => {
          for (let i = 0; i < tree.length; i++) {
            if (tree[i] === nodeToRemove) {
              tree.splice(i, 1)
              return true
            }``
            if (tree[i].children && tree[i].children.length > 0) {
              if (removeNode(tree[i].children, nodeToRemove)) {
                return true
              }
            }
          }
          return false
        }
        removeNode([this.currentTree], node)
        this.$forceUpdate()
      },
      //向后端发送currentTree数据
      saveTree() {
        fetch('/api/save-tree', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(this.currentTree)
        })
        .then(response => response.json())
        .then(data => {
          console.log('树结构已成功保存到后端');
        })
        .catch((error) => {
          console.error('保存树结构时出错:', error);
        });
      },
      //用于从服务器加载树结构数据
      loadTree() {
        fetch('/api/load-tree')
          .then(response => response.json())
          .then(data => {
            this.currentTree = data;
          })
          .catch(error => {
            console.error('加载失败', error);
          });
      }
    },
    mounted() {
      // 组件挂载时默认使用 defaultTree4
      if (this.readOnly) {
      this.currentTree = JSON.parse(JSON.stringify(this.initialTree))
    } else {
      this.currentTree = JSON.parse(JSON.stringify(defaultTree4))
    }
    }
  }
  </script>
  
  <style scoped>
  .tree-chart {
    text-align: center;
  }
  
  .template-selector,
  .zoom-controls {
    margin-bottom: 1rem;
  }
  
  .tree-container-wrapper {
    overflow: auto;
  }
  
  .tree-container {
    transform-origin: 0 0;
  }
  </style>
    
    <style lang="scss">
  .tree-chart {
    height: 100%;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 50px;
    overflow-x: auto;
    overflow-y: auto;
    min-height: 100vh;
  
    .zoom-controls {
      position: fixed;
      top: 10px;
      right: 10px;
      z-index: 1000;
  
      button {
        margin-left: 5px;
        padding: 5px 10px;
        background-color: #f0f0f0;
        border: 1px solid #ccc;
        border-radius: 3px;
        cursor: pointer;
  
        &:hover {
          background-color: #e0e0e0;
        }
      }
    }
  
    .tree-container-wrapper {
      width: 100%; // 可以调整这个值来改变容器宽度
      height: 50vh; // 可以调整这个值来改变容器高度
      border: 2px solid #ccc; // 添加边框
      background-color: beige; //背景色
      border-radius: 10px; // 添加圆角
      overflow: auto; // 允许内容溢出时滚动
      margin-top: -50px; 
      margin-right: 10px;
      position: relative; // 添加相对定位
    }
  
    .tree-container {
      transform-origin: center top;
      transition: transform 0.3s ease;
      min-width: 100%;
      min-height: 100%;
      display: flex;
      justify-content: center; // 水平居中
      align-items: flex-start; // 从顶部开始
      justify-content: center;
      padding: 20px;
      box-sizing: border-box;
      position: absolute; // 使用绝对定位
      left: 0; // 将左边缘置于容器中心
      transform: translateX(-50%); // 向左移动自身宽度的一半
      
    }
  }
  
  // 响应式调整
  @media (max-width: 768px) {
    .tree-chart {
      padding-top: 20px;
  
  
      .tree-container-wrapper {
        width: 100%;
        height: 70vh;
      }
    }
  }
  </style>