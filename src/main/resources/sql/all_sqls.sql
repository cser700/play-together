在此统一管理所有 sql，优点有：
1：避免在 JFinalClubConfig 中一个个添加 sql 模板文件
2：免除在实际的模板文件中书写 namespace，以免让 sql 定义往后缩进一层
3：在此文件中还可以通过 define 指令定义一些通用模板函数，供全局共享
   例如定义通用的 CRUD 模板函数

#namespace("user")
#include("user.sql")
#end

#namespace("sign")
#include("sign.sql")
#end

#namespace("account")
#include("account.sql")
#end

#namespace("deal")
#include("deal.sql")
#end

#namespace("gift")
#include("gift.sql")
#end

#namespace("task")
#include("task.sql")
#end

#namespace("taskAccept")
#include("task_accept.sql")
#end

#namespace("sms")
#include("sms.sql")
#end

#namespace("company")
#include("company.sql")
#end

#namespace("companyAccount")
#include("companyAccount.sql")
#end

#namespace("apply")
#include("apply.sql")
#end

#namespace("product")
#include("product.sql")
#end

#namespace("orders")
#include("orders.sql")
#end

#namespace("orderList")
#include("orderList.sql")
#end

#namespace("notice")
#include("notice.sql")
#end

#namespace("giftLike")
#include("giftLike.sql")
#end

#namespace("giftComment")
#include("giftComment.sql")
#end

#namespace("pay")
#include("pay.sql")
#end
