<template>
  <!-- 创建一个 div 元素，用于容纳 ECharts 图表 -->
  <div ref="chart" :style="{ width: '100%', height: '100%' }"></div>
</template>

<script>
import * as echarts from 'echarts'; // 导入 ECharts 库

export default {
  // 组件接收的属性（props）
  props: {
    option: {
      type: Object, // 属性类型为对象
      required: true, // 设置为必需属性
    },
  },
  data() {
    return {
      chart: null, // 用于存储 ECharts 实例
    };
  },
  mounted() {
    // 组件挂载完成后执行
    if (this.option) {
      // 检查 option 是否存在
      this.chart = echarts.init(this.$refs.chart); // 初始化 ECharts 实例
      this.chart.setOption(this.option); // 设置 ECharts 图表配置
    }
  },
  watch: {
    // 监听 option 属性的变化
    option: {
      deep: true, // 深度监听，包括内部的变化
      handler(newOption) {
        // 当 option 发生变化时，调用此函数
        if (this.chart) {
          // 如果 ECharts 实例已存在
          this.chart.setOption(newOption); // 更新 ECharts 图表配置
        } else {
          // 如果 ECharts 实例不存在
          this.chart = echarts.init(this.$refs.chart); // 初始化 ECharts 实例
          this.chart.setOption(newOption); // 设置 ECharts 图表配置
        }
      },
    },
  },
  beforeDestroy() {
    // 组件销毁前执行
    if (this.chart) {
      // 如果 ECharts 实例存在
      this.chart.dispose(); // 释放 ECharts 实例资源
    }
  },
};
</script>

<style scoped>
/* 在此处添加任何必要的样式 */
</style>
  