var express = require("express");
const bodyParser = require("body-parser");
const routes = require("./routes/routes");

var app = express();
app.use("/api", routes)

app.listen(3000, () => {
 console.log("Server running on port 3000");
});

module.exports = app