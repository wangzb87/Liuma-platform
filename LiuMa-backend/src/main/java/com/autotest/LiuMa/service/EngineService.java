package com.autotest.LiuMa.service;

import com.autotest.LiuMa.common.constants.EngineStatus;
import com.autotest.LiuMa.common.constants.EngineType;
import com.autotest.LiuMa.common.constants.ReportStatus;
import com.autotest.LiuMa.common.exception.DuplicateContentException;
import com.autotest.LiuMa.database.domain.Engine;
import com.autotest.LiuMa.database.mapper.EngineMapper;
import com.autotest.LiuMa.database.mapper.ReportMapper;
import com.autotest.LiuMa.database.mapper.TaskMapper;
import com.autotest.LiuMa.dto.EngineDTO;
import com.autotest.LiuMa.dto.TaskDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class EngineService {

    @Resource
    private EngineMapper engineMapper;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private ReportMapper reportMapper;

    public Engine saveEngine(Engine engine) {
        Engine oldEngine = engineMapper.getEngineByName(engine.getProjectId(), engine.getName());
        if(oldEngine != null){
            throw new DuplicateContentException("当前项目已有重名引擎");
        }
        engine.setId(UUID.randomUUID().toString().replace("-", ""));
        engine.setSecret(UUID.randomUUID().toString().replace("-",""));
        engine.setEngineType(EngineType.CUSTOM.toString()); // 默认注册自定义引擎
        engine.setStatus(EngineStatus.OFFLINE.toString());  // 默认离线状态
        engine.setCreateTime(System.currentTimeMillis());
        engine.setUpdateTime(System.currentTimeMillis());
        engine.setCreateUser(engine.getUpdateUser());
        engineMapper.saveEngine(engine);

        return engine;
    }

    public void deleteEngine(Engine engine) {engineMapper.deleteEngine(engine.getId());
    }

    public void stopEngineTask(String taskId) {
        reportMapper.updateReportStatusByTask(ReportStatus.DISCONTINUE.toString(), taskId);
        taskMapper.updateTask(ReportStatus.DISCONTINUE.toString(), taskId);
    }

    public void stopEngineAllTask(String engineId) {
        reportMapper.updateAllReportStatusByEngine(ReportStatus.DISCONTINUE.toString(), engineId);
        taskMapper.updateEngineAllTask(ReportStatus.DISCONTINUE.toString(), engineId);
    }

    public EngineDTO getEngineById(String id){
        EngineDTO engineDTO =  engineMapper.getEngineById(id);
        List<TaskDTO> taskDTOS = taskMapper.getTaskList(id);
        engineDTO.setTaskList(taskDTOS);
        return engineDTO;
    }

    public List<Engine> getAllCustomEngine(String projectId){
        List<Engine> engineList = engineMapper.getAllCustomEngine(projectId);
        Engine engine = new Engine();
        engine.setId(EngineType.SYSTEM.toString());
        engine.setName("系统引擎");
        engineList.add(0, engine);
        return engineList;
    }

    public List<EngineDTO> getEngineList(String projectId, String condition){
        if(condition != null && !condition.equals("")){
            condition = ("%"+condition+"%");
        }
        return engineMapper.getEngineList(projectId, condition);
    }


}