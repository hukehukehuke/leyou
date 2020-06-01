package com.leyou.pojo;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import lombok.Data;

import java.util.List;

@Data
public class SpuBo extends Spu {

    private String cname;
    private String bname;
    private SpuDetail spuDetail;
    private List<Sku> skus;
}
