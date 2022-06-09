package com.qdi.rajapay.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ProductListData extends BaseResponseData {

    List<ProductData> productList;
}
