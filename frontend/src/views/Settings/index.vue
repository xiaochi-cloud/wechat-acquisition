<template>
  <div class="settings-page">
    <el-card>
      <h2>系统设置</h2>
      
      <el-tabs>
        <el-tab-pane label="企微配置">
          <el-form label-width="150px" style="max-width: 600px; margin-top: 20px">
            <el-form-item label="企业 ID (CorpId)">
              <el-input v-model="config.corpId" placeholder="wwxxxxxxxxxxxxxx" />
            </el-form-item>
            <el-form-item label="应用 ID (AgentId)">
              <el-input v-model="config.agentId" placeholder="1000001" />
            </el-form-item>
            <el-form-item label="应用 Secret">
              <el-input v-model="config.secret" type="password" show-password />
            </el-form-item>
            <el-form-item label="Token">
              <el-input v-model="config.token" placeholder="自定义 token" />
            </el-form-item>
            <el-form-item label="EncodingAESKey">
              <el-input v-model="config.aesKey" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave">保存配置</el-button>
              <el-button @click="handleTest">测试连接</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="大模型配置">
          <el-form label-width="150px" style="max-width: 600px; margin-top: 20px">
            <el-form-item label="服务商">
              <el-select v-model="config.llmProvider" style="width: 100%">
                <el-option label="通义千问" value="dashscope" />
                <el-option label="DeepSeek" value="deepseek" />
              </el-select>
            </el-form-item>
            <el-form-item label="API Key">
              <el-input v-model="config.llmKey" type="password" show-password />
            </el-form-item>
            <el-form-item label="模型">
              <el-select v-model="config.llmModel" style="width: 100%">
                <el-option label="qwen-max" value="qwen-max" />
                <el-option label="qwen-plus" value="qwen-plus" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="频率限制">
          <el-form label-width="150px" style="max-width: 600px; margin-top: 20px">
            <el-form-item label="每日加人上限">
              <el-input-number v-model="config.dailyLimit" :min="1" :max="500" />
            </el-form-item>
            <el-form-item label="每小时加人上限">
              <el-input-number v-model="config.hourlyLimit" :min="1" :max="100" />
            </el-form-item>
            <el-form-item label="最小间隔 (秒)">
              <el-input-number v-model="config.minInterval" :min="10" :max="300" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'

const config = reactive({
  corpId: '',
  agentId: '',
  secret: '',
  token: '',
  aesKey: '',
  llmProvider: 'dashscope',
  llmKey: '',
  llmModel: 'qwen-max',
  dailyLimit: 50,
  hourlyLimit: 10,
  minInterval: 30
})

const handleSave = () => {
  ElMessage.success('配置保存成功')
}

const handleTest = () => {
  ElMessage.info('测试连接功能开发中')
}
</script>
