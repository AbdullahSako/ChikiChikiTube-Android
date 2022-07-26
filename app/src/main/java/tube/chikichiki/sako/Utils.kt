package tube.chikichiki.sako

import android.content.Context
import android.content.SharedPreferences

object Utils {

    //stores id of liked/disliked comments
     var likes:MutableSet<String>? = null
     var disLikes:MutableSet<String>? = null
     var IsInPipMode=false


    fun initializeLikesAndDislikes(context: Context){

        //get liked/disliked comment ids from shared preferences and add them into a list
        val sharedPref = context.getSharedPreferences("activity.MainActivity", Context.MODE_PRIVATE)



        val tempLike = sharedPref.getStringSet(context.getString(R.string.likes), mutableSetOf())
        val tempDisLike = sharedPref.getStringSet(context.getString(R.string.dislikes), mutableSetOf())

        likes = tempLike?.toMutableSet()
        disLikes=tempDisLike?.toMutableSet()





    }
    fun addLikeToShredPref(context: Context,commentId:Int){
        val sharedPref = context.getSharedPreferences("activity.MainActivity", Context.MODE_PRIVATE)


        likes?.add(commentId.toString())

        val editor:SharedPreferences.Editor= sharedPref.edit()
        editor.putStringSet(context.getString(R.string.likes), likes)
        editor.apply()




    }

    fun addDisLikeToShredPref(context: Context,commentId:Int){
        val sharedPref = context.getSharedPreferences("activity.MainActivity", Context.MODE_PRIVATE)

        disLikes?.add(commentId.toString())

        val editor:SharedPreferences.Editor= sharedPref.edit()
        editor.putStringSet(context.getString(R.string.dislikes), disLikes)
        editor.apply()


    }
    fun removeLikeFromShredPref(context: Context,commentId:Int){
        val sharedPref = context.getSharedPreferences("activity.MainActivity", Context.MODE_PRIVATE)

        likes?.remove(commentId.toString())

        val editor:SharedPreferences.Editor= sharedPref.edit()
        editor.putStringSet(context.getString(R.string.likes), likes)
        editor.apply()




    }
    fun removeDisLikeFromShredPref(context: Context,commentId:Int){
        val sharedPref = context.getSharedPreferences("activity.MainActivity", Context.MODE_PRIVATE)

        disLikes?.remove(commentId.toString())

        val editor:SharedPreferences.Editor= sharedPref.edit()
        editor.putStringSet(context.getString(R.string.dislikes), disLikes)
        editor.apply()

    }

    //used to generate nicknames
    val animals= arrayOf(
        "Aardvark",
        "Albatross",
        "Alligator",
        "Alpaca",
        "Ant",
        "Anteater",
        "Antelope",
        "Ape",
        "Armadillo",
        "Donkey",
        "Baboon",
        "Badger",
        "Barracuda",
        "Bat",
        "Bear",
        "Beaver",
        "Bee",
        "Bison",
        "Boar",
        "Buffalo",
        "Butterfly",
        "Camel",
        "Capybara",
        "Caribou",
        "Cassowary",
        "Cat",
        "Caterpillar",
        "Cattle",
        "Chamois",
        "Cheetah",
        "Chicken",
        "Chimpanzee",
        "Chinchilla",
        "Chough",
        "Clam",
        "Cobra",
        "Cockroach",
        "Cod",
        "Cormorant",
        "Coyote",
        "Crab",
        "Crane",
        "Crocodile",
        "Crow",
        "Curlew",
        "Deer",
        "Dinosaur",
        "Dog",
        "Dogfish",
        "Dolphin",
        "Dotterel",
        "Dove",
        "Dragonfly",
        "Duck",
        "Dugong",
        "Dunlin",
        "Eagle",
        "Echidna",
        "Eel",
        "Eland",
        "Elephant",
        "Elk",
        "Emu",
        "Falcon",
        "Ferret",
        "Finch",
        "Fish",
        "Flamingo",
        "Fly",
        "Fox",
        "Frog",
        "Gaur",
        "Gazelle",
        "Gerbil",
        "Giraffe",
        "Gnat",
        "Gnu",
        "Goat",
        "Goldfinch",
        "Goldfish",
        "Goose",
        "Gorilla",
        "Goshawk",
        "Grasshopper",
        "Grouse",
        "Guanaco",
        "Gull",
        "Hamster",
        "Hare",
        "Hawk",
        "Hedgehog",
        "Heron",
        "Herring",
        "Hippopotamus",
        "Hornet",
        "Horse",
        "Human",
        "Hummingbird",
        "Hyena",
        "Ibex",
        "Ibis",
        "Jackal",
        "Jaguar",
        "Jay",
        "Jellyfish",
        "Kangaroo",
        "Kingfisher",
        "Koala",
        "Kookabura",
        "Kouprey",
        "Kudu",
        "Lapwing",
        "Lark",
        "Lemur",
        "Leopard",
        "Lion",
        "Llama",
        "Lobster",
        "Locust",
        "Loris",
        "Louse",
        "Lyrebird",
        "Magpie",
        "Mallard",
        "Manatee",
        "Mandrill",
        "Mantis",
        "Marten",
        "Meerkat",
        "Mink",
        "Mole",
        "Mongoose",
        "Monkey",
        "Moose",
        "Mosquito",
        "Mouse",
        "Mule",
        "Narwhal",
        "Newt",
        "Nightingale",
        "Octopus",
        "Okapi",
        "Opossum",
        "Oryx",
        "Ostrich",
        "Otter",
        "Owl",
        "Oyster",
        "Panther",
        "Parrot",
        "Partridge",
        "Peafowl",
        "Pelican",
        "Penguin",
        "Pheasant",
        "Pig",
        "Pigeon",
        "Pony",
        "Porcupine",
        "Porpoise",
        "Quail",
        "Quelea",
        "Quetzal",
        "Rabbit",
        "Raccoon",
        "Rail",
        "Ram",
        "Rat",
        "Raven",
        "Red deer",
        "Red panda",
        "Reindeer",
        "Rhinoceros",
        "Rook",
        "Salamander",
        "Salmon",
        "Sand Dollar",
        "Sandpiper",
        "Sardine",
        "Scorpion",
        "Seahorse",
        "Seal",
        "Shark",
        "Sheep",
        "Shrew",
        "Skunk",
        "Snail",
        "Snake",
        "Sparrow",
        "Spider",
        "Spoonbill",
        "Squid",
        "Squirrel",
        "Starling",
        "Stingray",
        "Stinkbug",
        "Stork",
        "Swallow",
        "Swan",
        "Tapir",
        "Tarsier",
        "Termite",
        "Tiger",
        "Toad",
        "Trout",
        "Turkey",
        "Turtle",
        "Viper",
        "Vulture",
        "Wallaby",
        "Walrus",
        "Wasp",
        "Weasel",
        "Whale",
        "Wildcat",
        "Wolf",
        "Wolverine",
        "Wombat",
        "Woodcock",
        "Woodpecker",
        "Worm",
        "Wren",
        "Yak",
        "Zebra"
    )
}