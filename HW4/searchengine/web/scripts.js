const baseUrl = 'http://localhost:3000/api/search'

$(document).ready(function(){
    $("form").submit(function(event) {
        event.preventDefault()
        var values = {};
        let queryString = ''
        $.each($('#search-form').serializeArray(), function(i, field) {
            values[field.name] = field.value;
        });

        $.each(values, function(key, value) {
            if (value !== '') {
                if (queryString !== '') queryString += '&'
                queryString += key + '=' + value
            }
        })
        solrRequest(queryString)
    })
})

function solrRequest(queryString) {
    let url = baseUrl + '?' + queryString
    $.get(url, function(data, status) {
        let results = data.data.docs
        renderSearchResults(results)
    })
    .fail(function(jqXHR, textStatus, error) {
        alert(jqXHR.responseJSON.message)
    })
}

function renderSearchResults(results) {
    let html = ''
    if (results === undefined || results.length === 0) {
        html += '<div class="card mb-3">'
        html += '   <div class="card-body text-center">'
        html += '       No information found.'
        html += '   </div>'
        html += '</div>'
    } else {
        $.each(results, function(index, data) {
            html += '<div class="card mb-3">'
            html += '   <div class="card-body">'
            html += '       <div><label>Title: </label> <a href="' + data.og_url + '" target="_blank">' + data.title + '</a></div>'
            html += '       <div><label>URL: </label> <a href="' + data.og_url + '" target="_blank">' + data.og_url + '</a></div>'
            html += '       <div><label>Description: </label> ' + (data.og_description === undefined || data.og_description === '' ? 'N/A' : data.og_description[0]) + '</div>'
            html += '       <div><label>ID: </label> ' + data.id + '</div>'
            html += '   </div>'
            html += '</div>'
        })
    }

    $('#result').html(html)
}