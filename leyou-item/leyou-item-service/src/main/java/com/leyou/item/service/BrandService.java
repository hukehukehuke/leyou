package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据条件分页查询品牌信息
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }
        //添加分页条件
        PageHelper.startPage(page, rows);
        //添加排序
        if (StringUtils.isNotEmpty(sortBy)) {
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }

        List<Brand> brands = this.brandMapper.selectByExample(example);

        PageInfo<Brand> pageInfo = new PageInfo<>(brands);

        //构造一个分页结果集返回
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 新增品牌
     * @param brand
     * @param cid
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveBrand(Brand brand, List<Long> cid) {
        Boolean flag = this.brandMapper.insertSelective(brand) == 1;
        if(flag){
           cid.forEach(tempCid -> {
               this.brandMapper.insertCategoryAndBrand(tempCid,brand.getId());
           });
        }
    }

    public List<Brand> queryBrandsByCid(Long cid) {

       return this.brandMapper.selectBrandsByCid(cid);
    }
}
