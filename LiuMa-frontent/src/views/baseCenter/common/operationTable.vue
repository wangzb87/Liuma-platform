/**
 * 公共组件  控件列表
 */
<template>
  <div>
    <el-table size="small" :data="operationData" v-loading="loading">
        <el-table-column prop="index" label="序号" align="center" width="50px"/>
        <el-table-column prop="name" label="控件名称"/>
        <el-table-column prop="from" label="控件类型">
            <template slot-scope="scope">
                <span v-if="scope.row.from==='system'">系统内置控件</span>
                <span v-if="scope.row.from==='custom'">用户定义控件</span>
            </template>
        </el-table-column>
        <el-table-column prop="description" label="控件说明" min-width="200px"/>
        <el-table-column prop="username" label="创建人"/>
        <el-table-column prop="updateTime" label="更新时间" width="150px"/>
        <el-table-column fixed="right" align="operation" label="操作" width="100px">
            <template slot-scope="scope">
                <el-button type="text" size="mini" @click="viewOperation(scope.row)">查看</el-button>
                <el-button type="text" size="mini" v-if="scope.row.createUser ===currentUser" @click="deleteOperation(scope.row)">删除</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination v-bind:child-msg="pageparam" @callFather="callFather"></Pagination>
  </div>
</template>

<script>
import Pagination from '@/views/common/components/pagination'
export default {
    name: "OperationTable",
    components: {
        Pagination
    },
    props:{
        operationData: Array,
        loading: Boolean,
        pageparam: Object,
    },
    data(){
        return {
            currentUser: ""
        }
    },
    created() {
        this.currentUser = this.$store.state.userInfo.id;
    },
    methods: {
        // 获取列表数据方法
        callFather(param) {
            this.$emit("callFather", param);
        },
        // 查看控件
        viewOperation(row){
            this.$router.push({path: '/common/operationManage/edit/' + row.id});
        },
        // 删除控件
        deleteOperation(row){
            this.$confirm('控件删除后相关用例无法执行 确定要删除控件吗?', '删除提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
            .then(() => {
                this.$emit("deleteOperation", row);
            })
            .catch(() => {
                this.$message.success("取消成功");
            })
        },
    }
}
</script>

<style scoped>

</style>
