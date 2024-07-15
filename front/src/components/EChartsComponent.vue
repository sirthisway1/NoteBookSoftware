<template>
    <!-- ����һ�� div Ԫ�أ��������� ECharts ͼ�� -->
    <div ref="chart" :style="{ width: '100%', height: '100%' }"></div>
  </template>
  
  <script>
  import * as echarts from 'echarts'; // ���� ECharts ��
  
  export default {
    // ������յ����ԣ�props��
    props: {
      option: {
        type: Object, // ��������Ϊ����
        required: true, // ������Ϊ������
      },
    },
    data() {
      return {
        chart: null, // ���ڴ洢 ECharts ʵ��
      };
    },
    mounted() {
      // ���������ɺ�ִ��
      if (this.option) {
        // ��� option �Ƿ����
        this.chart = echarts.init(this.$refs.chart); // ��ʼ�� ECharts ʵ��
        this.chart.setOption(this.option); // ���� ECharts ��������
      }
    },
    watch: {
      // ���� option ���Եı仯
      option: {
        deep: true, // ��ȼ��������ڲ��ı仯
        handler(newOption) {
          // �� option �����仯ʱ���ô˺���
          if (this.chart) {
            // ��� ECharts ʵ���Ѵ���
            this.chart.setOption(newOption); // ���� ECharts ��������
          } else {
            // ��� ECharts ʵ��������
            this.chart = echarts.init(this.$refs.chart); // ��ʼ�� ECharts ʵ��
            this.chart.setOption(newOption); // ���� ECharts ��������
          }
        },
      },
    },
    beforeDestroy() {
      // �������ǰִ��
      if (this.chart) {
        // ��� ECharts ʵ������
        this.chart.dispose(); // �ͷ� ECharts ʵ����
      }
    },
  };
  </script>
  
  <style scoped>
  /* �ڴ˴������κα�Ҫ����ʽ */
  </style>
  