package me.konyaco.keeptally.viewmodel.model

object Colors {
    val deepPurple = longArrayOf(
        0xFFEDE7F6,
        0xFFD1C4E9,
        0xFFB39DDB,
        0xFF9575CD,
        0xFF7E57C2,
        0xFF673AB7,
        0xFF5E35B1,
        0xFF512DA8,
        0xFF4527A0,
        0xFF311B92
    )
    val cyan = longArrayOf(
        0xFFE0F7FA,
        0xFFB2EBF2,
        0xFF80DEEA,
        0xFF4DD0E1,
        0xFF26C6DA,
        0xFF00BCD4,
        0xFF00ACC1,
        0xFF0097A7,
        0xFF00838F,
        0xFF006064
    )


    val blue = longArrayOf(
        0xFFE3F2FD,
        0xFFBBDEFB,
        0xFF90CAF9,
        0xFF64B5F6,
        0xFF42A5F5,
        0xFF2196F3,
        0xFF1E88E5,
        0xFF1976D2,
        0xFF1565C0,
        0xFF0D47A1
    )

    val green = longArrayOf(
        0xFFE8F5E9,
        0xFFC8E6C9,
        0xFFA5D6A7,
        0xFF81C784,
        0xFF66BB6A,
        0xFF4CAF50,
        0xFF43A047,
        0xFF388E3C,
        0xFF2E7D32,
        0xFF1B5E20
    )

    val lightGreen = longArrayOf(
        0xFFF1F8E9,
        0xFFDCEDC8,
        0xFFC5E1A5,
        0xFFAED581,
        0xFF9CCC65,
        0xFF8BC34A,
        0xFF7CB342,
        0xFF689F38,
        0xFF558B2F,
        0xFF33691E
    )

    val lime = longArrayOf(
        0xFFF9FBE7,
        0xFFF0F4C3,
        0xFFE6EE9C,
        0xFFDCE775,
        0xFFD4E157,
        0xFFCDDC39,
        0xFFC0CA33,
        0xFFAFB42B,
        0xFF9E9D24,
        0xFF827717
    )


    val expColorSet = arrayOf(deepPurple, cyan, blue)
    val incomeColorSet = arrayOf(green, lightGreen, lime)

    val expLightColors: List<Long> = expColorSet.flatMap { it.slice(setOf(1, 3, 5, 7, 9)) }
    val expDarkColors: List<Long> = expColorSet.flatMap { it.slice(setOf(0, 2, 4, 6, 8)).reversed() }


    val incomeLightColors: List<Long> = incomeColorSet.flatMap { it.slice(setOf(1, 3, 5, 7, 9)) }
    val incomeDarkColors: List<Long> = incomeColorSet.flatMap { it.slice(setOf(0, 2, 4, 6, 8)).reversed() }

    val expColors: List<Pair<Long, Long>> = expLightColors.zip(expDarkColors).shuffled()
    val incomeColors: List<Pair<Long, Long>> = incomeLightColors.zip(incomeDarkColors).shuffled()
}