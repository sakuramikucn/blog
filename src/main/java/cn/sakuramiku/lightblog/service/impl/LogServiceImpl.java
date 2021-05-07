package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.entity.Log;
import cn.sakuramiku.lightblog.mapper.LogMapper;
import cn.sakuramiku.lightblog.service.LogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 日志服务实现
 *
 * @author lyy
 */
@Service
public class LogServiceImpl implements LogService {

    @Resource
    private LogMapper logMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long writeLog(@NonNull Log log) {
        long id = IdGenerator.nextId();
        log.setCreateTime(LocalDateTime.now());
        logMapper.insert(log);
        return id;
    }

    @Override
    public Long writeLog(@NonNull String ref, @NonNull String operator, @NonNull String action, String what) {
        return writeLog(ref, operator, action, what, null);
    }

    @Override
    public Long writeLog(@NonNull String ref, @NonNull String operator, @NonNull String action, String what, String note) {
        Log log = new Log();
        log.setReference(ref);
        log.setOperator(operator);
        log.setAction(action);
        log.setWhat(what);
        log.setNote(note);
        return writeLog(log);
    }

    @Override
    public PageInfo<Log> getLogs(@NonNull String ref, Integer page, Integer pageSize) {
        return searchLog(null, ref, null, null, page, pageSize);
    }

    @Override
    public PageInfo<Log> searchLog(String category, String ref, LocalDateTime begin, LocalDateTime end,
                                   Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            Page<Object> objects = PageHelper.startPage(page, pageSize, true);
            objects.setOrderBy("modified_time DESC");
        }
        List<Log> logs = logMapper.search(ref,category, begin, end);
        return PageInfo.of(logs);
    }
}
