package cn.sakuramiku.lightblog.service.impl;

import cn.sakuramiku.lightblog.annotation.OnCacheChange;
import cn.sakuramiku.lightblog.common.annotation.LogConfig;
import cn.sakuramiku.lightblog.common.annotation.WriteLog;
import cn.sakuramiku.lightblog.common.util.IdGenerator;
import cn.sakuramiku.lightblog.entity.Tag;
import cn.sakuramiku.lightblog.exception.BusinessException;
import cn.sakuramiku.lightblog.mapper.TagMapper;
import cn.sakuramiku.lightblog.model.BatchInsertParam;
import cn.sakuramiku.lightblog.service.TagService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签服务实现
 *
 * @author lyy
 */
@LogConfig(reference = "tag",name = "标签")
@CacheConfig(cacheNames = "light_blog:tag", keyGenerator = "simpleKeyGenerator")
@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagMapper tagMapper;
    @Lazy
    @Resource
    private TagService tagService;

    @WriteLog(action = WriteLog.Action.INSERT)
    @CachePut(key = "#result.id",unless = "null  == #result")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Tag saveTag(@NonNull String name) {
        long id = IdGenerator.nextId();
        Tag tag = new Tag();
        tag.setId(id);
        tag.setCreateTime(LocalDateTime.now());
        tag.setName(name);
        Boolean insert = tagMapper.insert(tag);
        if (insert){
            return getTag(id);
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.UPDATE)
    @CachePut(key = "#result.id",unless = "null  == #result")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Tag updateTag(@NonNull Long id, @NonNull String name) {
        Boolean update = tagMapper.update(id, name);
        if (update){
            return getTag(id);
        }
        return null;
    }

    @WriteLog(action = WriteLog.Action.DELETE)
    @CacheEvict(key = "#id")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean removeTag(@NonNull Long id) throws BusinessException {
        int articleCount = tagMapper.articleCount(id);
        if (articleCount > 0){
            throw new BusinessException("该标签还有文章在用哦");
        }
        return tagMapper.delete(id);
    }

    @Cacheable(key = "#id", unless = "null == #result")
    @Override
    public Tag getTag(@NonNull Long id) {
        return tagMapper.get(id);
    }

    @Override
    public Tag getTagByName(String name) {
        return tagMapper.getTagByName(name);
    }

    @OnCacheChange
    @Cacheable(unless = "null == #result || 0 == #result.total")
    @Override
    public PageInfo<Tag> search(Long articleId, String keyword, Integer page, Integer pageSize) {
        if (null != page && null != pageSize) {
            Page<Object> objects = PageHelper.startPage(page, pageSize, true);
            objects.setOrderBy("modified_time DESC");
        }
        List<Tag> tags;
        if (null != articleId) {
            tags = tagMapper.find(articleId, keyword, null, null);
        } else {
            tags = tagMapper.search(keyword, null, null);

        }
        return PageInfo.of(tags);
    }

    @Override
    public PageInfo<Tag> search(Long articleId, String keyword) {
        return search(articleId, keyword, null, null);
    }

    @CachePut
    @WriteLog(action = WriteLog.Action.INSERT)
    @Override
    public Boolean batchInsert(List<BatchInsertParam> params) {
        return tagMapper.batchInsert(params);
    }

    @Override
    public Boolean deleteForArticle(Long articleId) {
        return tagMapper.deleteForArticle(articleId);
    }

}
