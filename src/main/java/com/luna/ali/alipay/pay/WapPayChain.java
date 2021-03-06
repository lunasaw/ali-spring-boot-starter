/**
 * Copyright 2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luna.ali.alipay.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;

/**
 * ClassName: WapPayChain
 * Description:
 * date: 2019/12/24 20:10
 *
 * @author ThierrySquirrel
 * @since JDK 1.8
 */

public class WapPayChain {
    private AlipayClient           alipayClient;
    private AlipayTradeWapPayModel alipayTradeWapPayModel;

    public AlipayClient getAlipayClient() {
        return alipayClient;
    }

    public void setAlipayClient(AlipayClient alipayClient) {
        this.alipayClient = alipayClient;
    }

    public AlipayTradeWapPayModel getAlipayTradeWapPayModel() {
        return alipayTradeWapPayModel;
    }

    public void setAlipayTradeWapPayModel(AlipayTradeWapPayModel alipayTradeWapPayModel) {
        this.alipayTradeWapPayModel = alipayTradeWapPayModel;
    }

    public WapPayChain() {}

    public WapPayChain(AlipayClient alipayClient, AlipayTradeWapPayModel alipayTradeWapPayModel) {
        this.alipayClient = alipayClient;
        this.alipayTradeWapPayModel = alipayTradeWapPayModel;
    }

    /**
     * Generate Forms
     * <p>
     * ็ๆ่กจๅ
     *
     * @param returnUrl After the transaction is completed, the page will take the initiative to jump to the HTTP /
     * HTTPS path specified in the merchant server
     * ไบคๆๅฎๆๅ้กต้ขไธปๅจ่ทณ่ฝฌ,ๅๆทๆๅกๅจ้ๆๅฎ็้กต้ขhttp/https่ทฏๅพ
     * @param notifyUrl AliPay Server Initiatively Tells The Http/Https Path Specified In The Merchant Server.
     * ๆฏไปๅฎๆๅกๅจไธปๅจ้็ฅๅๆทๆๅกๅจ้ๆๅฎ็้กต้ขhttp/https่ทฏๅพ
     * @return String
     * @throws AlipayApiException AlipayApiException
     */
    public String pay(String returnUrl, String notifyUrl) throws AlipayApiException {
        AlipayTradeWapPayRequest payRequest = new AlipayTradeWapPayRequest();
        payRequest.setReturnUrl(returnUrl);
        payRequest.setNotifyUrl(notifyUrl);
        payRequest.setBizModel(alipayTradeWapPayModel);
        return alipayClient.pageExecute(payRequest).getBody();
    }

    /**
     * Custom Build PayRequest
     * <p>
     * ่ชๅฎไนๆๅปบPayRequest
     *
     * @param payRequest payRequest
     * @return String
     * @throws AlipayApiException AlipayApiException
     */
    public String pay(AlipayTradeWapPayRequest payRequest) throws AlipayApiException {
        payRequest.setBizModel(alipayTradeWapPayModel);
        return alipayClient.pageExecute(payRequest).getBody();
    }
}
