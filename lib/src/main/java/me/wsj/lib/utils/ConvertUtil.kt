package me.wsj.lib.utils

class ConvertUtil {
    companion object {
        @JvmStatic
        fun convert(code: Int): Int {
            var result = 0
            when (code) {
                100, 150 ->
                    result = 1  // 晴
                101, 102, 103, 153 ->
                    result = 2  // 多云
                104, 154 ->
                    result = 3  // 阴天
                300, 301, 306, 313, 315, 350, 399 ->
                    result = 4  // 雨
                305, 309, 314 ->
                    result = 40  // 小雨
                307, 308, 310, 311, 312, 316, 317, 318, 351 ->
                    result = 42  // 大雨
                302, 303, 304 ->
                    result = 5  // 雷雨
                in 400..457 ->
                    result = 6  // 雪
                500, 501, 509, 510, 514, 515 ->
                    result = 7  // 雾
                502, 511, 512, 513 ->
                    result = 8  // 霾
                in 503..508 ->
                    result = 9  // 沙尘
                else -> result = 0
            }
            return result
        }
    }
}