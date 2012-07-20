package json

class User(var id: String,
           var name: String,
           var firstName: String,
           var lastName: String,
           var link: String,
           var username: String,
           var picture: Picture,
           var email: String)

class Picture(var data: Data)
class Data(var url: String)