package cc.guider.architeature.common.utils.orderbiz;

/**
 * 模拟和其他模块通信的bean
 * @author JefferyLeng
 * @date 2019-08-21
 */
public class OrderAddressResponseBean {

    /**
     * resultcode : 200
     * reason : Return Successd!
     * result : {"province":"浙江","city":"杭州","areacode":"0571","zip":"310000","company":"中国移动","card":""}
     */

    private String resultcode;
    private String reason;
    private ResultBean result;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * province : 浙江
         * city : 杭州
         * areacode : 0571
         * zip : 310000
         * company : 中国移动
         * card :
         */

        private String province;
        private String city;
        private String areacode;
        private String zip;
        private String company;
        private String card;

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getAreacode() {
            return areacode;
        }

        public void setAreacode(String areacode) {
            this.areacode = areacode;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getCard() {
            return card;
        }

        public void setCard(String card) {
            this.card = card;
        }

        @Override
        public String toString() {
            return "ResultBean{" +
                    "province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", areacode='" + areacode + '\'' +
                    ", zip='" + zip + '\'' +
                    ", company='" + company + '\'' +
                    ", card='" + card + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "OrderAddressResponseBean{" +
                "resultcode='" + resultcode + '\'' +
                ", reason='" + reason + '\'' +
                ", result=" + result +
                '}';
    }
}
