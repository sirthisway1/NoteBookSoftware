<template>
  <!-- 定义一个模板，包含一个用于显示图表的 div 元素，宽度和高度设置为 100% -->
  <div ref="chart" :style="{ width: '100%', height: '100%' }"></div>
</template>

<script>
import * as echarts from 'echarts';  // 引入 ECharts 库

export default {
  // 组件默认导出
  props: {
    // 定义一个名为 option 的 prop，它是一个必需的对象类型
    option: {
      type: Object,
      required: true,
    },
  },
  data() {
    // 定义组件的数据，返回一个对象，其中包含一个 chart 属性，初始值为 null
    return {
      chart: null,
    };
  },
  mounted() {
    // 在组件挂载到 DOM 后立即执行的钩子函数
    if (this.option) {
      // 如果传入的 option 存在
      this.chart = echarts.init(this.$refs.chart); // 使用 ECharts 初始化图表
      this.chart.setOption(this.option); // 设置图表的配置项
    }
  },
  watch: {
    // 监听 option 属性的变化
    option: {
      deep: true, // 深度监听，确保嵌套对象的变化也能被监听到
      handler(newOption) {
        // 当 option 发生变化时执行的函数
        if (this.chart) {
          // 如果 chart 已经初始化
          this.chart.setOption(newOption); // 更新图表的配置项
        } else {
          // 如果 chart 尚未初始化
          this.chart = echarts.init(this.$refs.chart); // 初始化图表
          this.chart.setOption(newOption); // 设置图表的配置项
        }
      },
    },
  },
  beforeDestroy() {
    // 在组件销毁之前执行的钩子函数
    if (this.chart) {
      // 如果 chart 已经初始化
      this.chart.dispose(); // 销毁图表实例，释放资源
    }
  },
};
</script>

<style scoped>
/* 定义组件的局部样式，这里为空 */
</style>
