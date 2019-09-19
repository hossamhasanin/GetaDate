package com.hossam.hasanin.getadate.Externals

import java.util.HashMap


object Constants{
    fun getErrorMessages() : HashMap<ErrorTypes , String>{
        val ERROR_MESSAGES:HashMap<ErrorTypes , String> = HashMap<ErrorTypes , String>()
        ERROR_MESSAGES.put(ErrorTypes.FIRSTNAME_ERROR , "تأكد من كتابة الاسم الاول")
        ERROR_MESSAGES.put(ErrorTypes.SECONDNAME_ERROR , "تأكد من كتابة الاسم الثاني")
        ERROR_MESSAGES.put(ErrorTypes.AGE_ERROR , "تأكد من كتابة سنك الصحيح")
        ERROR_MESSAGES.put(ErrorTypes.PROFILE_IMAGE_ERROR , "يجب وضع صورة شخصية")
        ERROR_MESSAGES.put(ErrorTypes.EMAIL_ERROR , "تأكد من كتابة البريد الالكتروني بشكل صحيح")
        ERROR_MESSAGES.put(ErrorTypes.PASSWORD_ERROR , "تأكد من كتابة كلمة سر قوية و طويلة")
        ERROR_MESSAGES.put(ErrorTypes.USERNAME_ERROR , "تأكد من عدم ترك اسم المستخدم فارغا")
        ERROR_MESSAGES.put(ErrorTypes.LOCATION_MISSING , "يجب تحديد الموقع الجغرافي")
        ERROR_MESSAGES.put(ErrorTypes.FIREBASE_SERVER_ERROR , "خطأ في السرفر برجاء اعادة المحاولة بعد لحظات")
        ERROR_MESSAGES.put(ErrorTypes.WRONG_EMAIL_OR_PASSWORD , "تحقق من الاميل و كلمة المرور")
        ERROR_MESSAGES.put(ErrorTypes.ADDRESS_ERROR , "يجب ان تصف عنوانك")

        return ERROR_MESSAGES
    }

    const val LOCATION_REQUEST_CODE = 19

    const val MINIMUM_COMPARISION_DISTANCE = 1000

    const val NOTIFICATION_CHANALE_ID = 90

}

