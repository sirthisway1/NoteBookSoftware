<template>
    <table>
      <tr>
        <td
          :colspan="hasChild ? model.children.length * 2 : 1"
          :class="{ extend: hasChild && model.extend }"
        >
          <div class="card">
            <div class="title">
              <input v-model="model.resName" :readonly="!isEditing" @change="updateContent" class="name-input">
            </div>
            <div class="body">
              <textarea v-model="model.message" :readonly="!isEditing" @change="updateContent" class="content-textarea"></textarea>
            </div>
            <div class="footer">
              <div @click="$emit('on-add', model)" class="btn">添加</div>
              <div @click="toggleEdit" class="btn">{{ isEditing ? '保存' : '修改' }}</div>
              <div @click="$emit('on-remove', model)" class="btn">删除</div>
            </div>
          </div>
          <div class="extend_handle" v-if="hasChild" @click="toggleExtend">{{ model.extend ? '隐藏' : '展开' }}</div>
        </td>
      </tr>
      
      <tr v-if="hasChild && model.extend">
        <td
          v-for="(item, index) in model.children"
          :key="index"
          colspan="2"
          class="child"
        >
          <!--跨层级监听事件 v-on="$listeners"（.native原生事件无效） -->
          <transition name="fade">
            <TreeChart :model="item"
                v-on="$attrs"
                @on-add="$emit('on-add', $event)"
                @on-update="$emit('on-update', $event)"
                @on-remove="$emit('on-remove', $event)"
            />
        </transition>
        </td>
      </tr>
    </table>
  </template>
  
  <script>
  import { readonly } from 'vue';
  
  export default {
    name: 'TreeChart',
    props: ['model'],
    data() {
      return {
        isEditing: false
      }
    },
    computed: {
      hasChild () {
        return this.model.children && this.model.children.length
      }
    },
    methods: {
      toggleExtend () {
        this.model.extend = !this.model.extend
      },
      toggleEdit() {
        this.isEditing = !this.isEditing
        if (!this.isEditing) {
         // 保存修改
         this.$emit('on-update', this.model)
        }
      },
      updateContent() {
        if (this.isEditing) {
            this.$emit('on-update', this.model)
        }
      }
    }
  }
  </script>
  
  <style lang="scss">
  .card {
    background: #74f5c3;
    border: 2px solid #ffffff;
    border-radius: 10px;
    margin: 0 auto;
    width: 200px;
    transition: box-shadow 0.5s, transform 0.3s; // 添加 transform 过渡效果
    box-shadow: 0 10px 10px rgba(0, 0, 0, 0.1); // 添加默认阴影
    &:hover {
      box-shadow: 0px 20px 20px rgba(0, 0, 0, 0.15); // 增强悬停时的阴影效果
      transform: translateY(-5px); // 添加轻微的上移效果
    }
  
    .title {
      padding: 10px 0;
      font-size: 12px;
  
      .name-input {
            width: 90%;
            text-align: center;
            border: none;
            outline: none;
            background: transparent;
            font-size: 14px;
            font-weight: bold;
            color: #333;
            padding: 5px;
            box-sizing: border-box;
        }
    }
  
    .body {
      height: 100px;
      background: #ffffff;
      width: auto;
      margin: 0 15px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 5px; // 添加圆角，使选中效果更美观
  
      .content-textarea {
            width: 100%;
            height: 90px;
            resize: none;
            border: none;
            outline: none;
            background: transparent;
            font-size: 12px;
            text-align: left;
            padding-top: 5px;
            padding-left: 10px;
            box-sizing: border-box;
            transition: background-color 0.3s, box-shadow 0.3s; // 添加过渡效果
  
            &:focus {
        background-color: rgba(116, 245, 195, 0.1); // 轻微的背景色变化
        box-shadow: 0 0 0 2px rgba(116, 245, 195, 0.5); // 添加聚焦时的轮廓效果
      }
  
      &:not(:disabled) {
        cursor: text; // 当不禁用时，显示文本光标
      }
  
      &:disabled {
        cursor: default; // 当禁用时，显示默认光标
        opacity: 0.7; // 轻微降低不可编辑时的透明度
      }
        }
  
        
    }
  
    .footer {
      display: flex;
      margin: 5px 15px;
      justify-content: space-between;
      .btn {
        font-size: 12px;
        color: #000;
        cursor: pointer;
        padding: 2px 6px;
        border-radius: 4px;
        transition: background 0.3s, color 0.3s;
        &:hover {
          background: #4d6d5d;
          color: #f6f6f6;
        }
      }
    }
  }
  .extend_handle {
    cursor: pointer;
    &:hover {
      text-decoration: underline;
    }
  }
  table {
    border-collapse: separate !important;
    border-spacing:  0 !important;
    margin: 0 auto;
  
    td {
      position: relative;
      vertical-align: top;
      padding: 0 0 70px 0;
      text-align: center;
      transition: all 0.3s;
      &.extend {   //父节点向下的线
        &::after {
          content: '';
          position: absolute;
          left: 50%; //节点中心
          bottom: 35px; //距离底部
          height: 35px; //高度
          border-left: 2px solid rgb(159, 186, 202);  //绘制实线
          transform: translate3d(-0.7px, 0, 0); //微调
        }
      }
      &.child {   //除两边节点外的其他节点上方线
        &::before {
          content: '';
          position: absolute;
          left: 50%;
          bottom: 100%;
          height: 35px;
          border-left: 2px solid rgb(159, 186, 202);
          transform: translate3d(-1px, 0, 0);
        }
        &::after {  //横线
          content: '';
          position: absolute;
          left: 0;
          right: 0;
          top: -35px;
          border-top: 2px solid rgb(159, 186, 202);
        }
        &:first-child:before,
        &:last-child:before {
          display: none;
        }
        &:first-child:after {  //最左边节点向右延伸的线
          left: 50%;
          height: 35px;
          border: 2px solid;
          border-color: rgb(159, 186, 202) transparent transparent rgb(159, 186, 202);
          border-radius: 6px 0 0 0;
          transform: translate3d(1px, 0, 0);
        }
        &:last-child:after {  //最右边向左边延伸的线
          right: 50%;
          height: 35px;
          border: 2px solid;
          border-color: rgb(159, 186, 202) rgb(159, 186, 202) transparent transparent;
          border-radius: 0 6px 0 0;
          transform: translate3d(-1px, 0, 0);
        }
        &:first-child.child:last-child::after {
          left: auto;
          border-radius: 0;
          border-color: transparent rgb(159, 186, 202) transparent transparent;
          transform: translate3d(1px, 0, 0);
        }
  
        &.root-node {
          margin-bottom: 30px;
          // 可以添加其他样式来突出根节点，比如不同的背景色等
        }
  
      }
    }
  }
  .fade-enter-active, .fade-leave-active {
    transition: opacity 0.5s;
  }
  .fade-enter, .fade-leave-to /* .fade-leave-active in <2.1.8 */ {
    opacity: 0;
  }
  
  // 响应式调整
  @media (max-width: 768px) {
    table {
      border-spacing: 10px 0 !important;
    }
    
    .card {
      width: 180px;
    }
  }
  
  </style>