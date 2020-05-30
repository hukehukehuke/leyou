package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父节点查询子节点
     *
     * @param pid
     * @return
     */
    public List<Category> queryCategoryById(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        return this.categoryMapper.select(category);
    }

    /**
     * 添加
     *
     * @param category
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int addCategory(Category category) {
        return this.categoryMapper.insert(category);
    }

    @Transactional(rollbackFor = Exception.class)
    public int editionCategory(Category category) {
        return this.categoryMapper.updateByPrimaryKeySelective(category);
    }

    public int deleteCategoryByPid(Long pid) {
        int i = 0;
        if (pid != null) {
            i = this.categoryMapper.deleteByPrimaryKey(pid);
        }
        return i;
    }

    public List<String> queryNameByIds(List<Long> ids) {
        List<Category> categoryList = this.categoryMapper.selectByIdList(ids);
        return categoryList.stream().map(category -> {return category.getName();}).collect(Collectors.toList());
    }

}
