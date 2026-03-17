<template>
  <el-card>
    <template #header><span>系统设置</span></template>
    <el-tabs>
      <el-tab-pane label="企微配置">
        <el-form label-width="150px">
          <el-form-item label="企业 ID"><el-input v-model="config.corpId" /></el-form-item>
          <el-form-item label="应用 ID"><el-input v-model="config.agentId" /></el-form-item>
          <el-form-item label="Secret"><el-input v-model="config.secret" type="password" /></el-form-item>
          <el-form-item><el-button type="primary" @click="handleSave">保存配置</el-button></el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="大模型配置">
        <el-form label-width="150px">
          <el-form-item label="服务商">
            <el-select v-model="config.llmProvider">
              <el-option label="通义千问" value="dashscope" />
              <el-option label="DeepSeek" value="deepseek" />
            </el-select>
          </el-form-item>
          <el-form-item label="API Key"><el-input v-model="config.llmKey" type="password" /></el-form-item>
          <el-form-item><el-button type="primary" @click="handleSave">保存配置</el-button></el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="频率限制">
        <el-form label-width="150px">
          <el-form-item label="每日加人上限"><el-input-number v-model="config.dailyLimit" :min="1" :max="500" /></el-form-item>
          <el-form-item label="最小间隔 (秒)"><el-input-number v-model="config.minInterval" :min="10" :max="300" /></el-form-item>
          <el-form-item><el-button type="primary" @click="handleSave">保存配置</el-button></el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'

const config = reactive({
  corpId: '',
  agentId: '',
  secret: '',
  llmProvider: 'dashscope',
  llmKey: '',
  dailyLimit: 50,
  minInterval: 30
})

const handleSave = () => ElMessage.success('配置已保存')
</script>
