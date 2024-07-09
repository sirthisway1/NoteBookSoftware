<template>
    <h3>示例</h3>
    <div>
      <NoteCreateTree :readOnly="true" :initialTree="yourTreeData"/>
    </div>
  </template>
  
  <script>
  import NoteCreateTree from './NoteCreateTree.vue'
  
  export default {
    components: {
        NoteCreateTree
    },
    data() {
      return {
        yourTreeData: {
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
      }
    }
  }
  </script>