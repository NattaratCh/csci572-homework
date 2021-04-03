var express = require("express");
var cors = require('cors')
const searchApi = require("./api/search.api")
const routes = require("./routes/routes");

var app = express();
app.use(cors())
app.use("/api", routes)

app.listen(3000, () => {
 console.log("Server running on port 3000");
 searchApi.loadUrlMapping()
});

module.exports = app