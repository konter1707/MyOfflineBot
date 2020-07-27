package com.example.myofflinebot.bots

class ProductCost {
    public fun productCost(recipe: String): String {
        var count:Int=0
        for (igredient in getIgredients(recipe)){
           for (product in setList()){
               if(igredient.product.equals(product.product)){
                   count+=product.cost
               }
           }
        }
        return count.toString()
    }
    fun getIgredients(text: String): List<ModelProductCost> {
        val list: MutableList<ModelProductCost> = mutableListOf()
        val igredients = text.split(",")
        for (igredient in igredients) {
            val product = igredient.split("-")[0].trim()
            val cost:Int = igredient.split("-")[1].toInt()
            list.add(ModelProductCost(product,cost))
        }
        return list
    }

    fun setList(): List<ModelProductCost> {
        val list = listOf<ModelProductCost>(
            ModelProductCost("Молоко", 46),
            ModelProductCost("Кефир", 23),
            ModelProductCost("Творог", 180),
            ModelProductCost("Сметана", 80),
            ModelProductCost("Мука", 35),
            ModelProductCost("Дрожжи", 15),
            ModelProductCost("Сахар", 35),
            ModelProductCost("Соль", 10),
            ModelProductCost("Репчетый лук", 30),
            ModelProductCost("Картошка", 25),
            ModelProductCost("Чеснок", 250),
            ModelProductCost("Марковь", 40),
            ModelProductCost("Капуста", 30),
            ModelProductCost("Яблоко", 90),
            ModelProductCost("Яйца", 45),
            ModelProductCost("Свинина", 350),
            ModelProductCost("Говядина", 250),
            ModelProductCost("Курица", 120),
            ModelProductCost("Черный перец", 15),
            ModelProductCost("Лавровый лист", 10),
            ModelProductCost("Гречка", 60),
            ModelProductCost("Рис", 45),
            ModelProductCost("Макароны", 35),
            ModelProductCost("Растительное масло", 80)
        )
        return list
    }

    data class ModelProductCost(
        val product: String, val cost: Int
    )
}
