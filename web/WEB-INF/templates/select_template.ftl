<html>
<head>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <script>
        $(document).ready(function(){
            var key = "";
            var page = 1;
            var addpage = 0;
            var numOfPages = 0;
            var bookID;

            if(${ADMIN?c}) {
                $(".admin").show();
                $("#prev").hide();
                $("#next").hide();
                $("#submitButton").hide();
                $("#addPage").hide();
                $("#submitForm").hide();
                $("#book").hide();
                $("#booktitle").hide();
                $("#titleSubmit").hide();
                $("#submitBook").hide();
                $("#ta").hide();
                $("#editBookInfo").hide();
                $("#submitInfo").hide();
                $(".bookinfo").hide();
                $("#backfromedit").hide();
                reloadBooks();
            }
            else {
                $(".admin").hide();
                $("#prev").hide();
                $("#addStory").hide();
                $("#addstory").hide();
                $("#next").hide();
                $("#submitButton").hide();
                $("#addPage").hide();
                $("#book").hide();
                $("#booktitle").hide();
                $("#titleSubmit").hide();
                $("#submitBook").hide();
                $("#ta").hide();
                $("#editBookInfo").hide();
                $("#submitInfo").hide();
                $(".bookinfo").hide();
                $("#submitForm").show();

            }
            function reloadBooks() {
                $.getJSON("/Program_3_war_exploded/storyRest//getkey/admin/admin", function (result) {
                    key = result.APIkey;
                    $.getJSON("/Program_3_war_exploded/storyRest/" + key + "/storyTitles", function (result) {
                        $.each(result.StoryList, function () {
                            $(".ebooks").append("<option>" + this['Book info'] + "</option>");
                        })
                    })
                });
            }

            $("#deleteForm").submit(function(evt){
                evt.preventDefault();
                var bookName = $("#deleteBook option:selected").text();
                var bookid;
                $.getJSON("/Program_3_war_exploded/storyRest/"+key+"/getStoryID/"+ bookName, function(idres){
                    bookid=idres.ID
                    $.ajax({
                        url: '/Program_3_war_exploded/storyRest/'+bookid,
                        type: 'DELETE'
                    });
                });
                location.reload(true);
            });
            $("#editForm").submit(function(evt) {
                $("#submitButton").show();
                $("#addPage").show();
                $("#editBookInfo").show();
                $(".bookinfo").hide();
                $("#ta").show();
                $("#book").hide();
                $("#booktitle").hide();
                $("#submitBook").hide();
                $("#titleSubmit").hide();
                evt.preventDefault();
                var ebookName = $("#editBook option:selected").text();
                $.getJSON("/Program_3_war_exploded/storyRest/"+key+"/getStoryID/"+ ebookName, function(idres){
                    $.getJSON("/Program_3_war_exploded/storyRest/"+key+"/story/"+idres.ID+"/1", function(bookContent){
                        //console.log(bookContent);
                        document.getElementById("ta").value = bookContent.Story;
                        bookID = idres.ID;
                        $.getJSON("/Program_3_war_exploded/storyRest//getPageNum/"+bookID, function(pageNum) {
                            numOfPages = pageNum.PG;
                            page = 1;
                            if (page == numOfPages) $("#next").hide();
                            else $("#next").show();
                            $("#prev").hide();
                        });

                        window.nextPage = function() {
                            page = page + 1;
                            $("#prev").show();
                            if (page == numOfPages) $("#next").hide();
                            $.getJSON("/Program_3_war_exploded/storyRest/"+key+"/story/"+bookID+"/"+page, function(bookContent){
                                console.log(bookContent);
                                document.getElementById("ta").value = bookContent.Story;
                            })
                        };

                        window.prevPage = function() {
                            page = page - 1;
                            $("#next").show();
                            if (page == 1) $("#prev").hide();
                            $.getJSON("/Program_3_war_exploded/storyRest/"+key+"/story/"+bookID+"/"+page, function(bookContent){
                                console.log(bookContent);
                                document.getElementById("ta").value = bookContent.Story;
                            })
                        };

                        window.submitPage = function() {
                            $.post("/Program_3_war_exploded/storyRest/editStory/"+page+"/"+bookID, document.getElementById("ta").value);
                            $("#submitButton").hide();
                            location.reload(true);
                        };

                        window.submitInfo = function() {
                            $.post("/Program_3_war_exploded/storyRest/editTitle/"+bookID+"/"+document.getElementById("bookinfot").value);
                            location.reload(true);
                        };

                        window.editBookInfo = function() {
                            $("#prev").hide();
                            $("#next").hide();
                            $("#ta").hide();
                            $("#submitButton").hide();
                            $("#addPage").hide();
                            $("#booktitle").hide();
                            $("#titleSubmit").hide();
                            $("#submitBook").hide();
                            $("#editBookInfo").hide();
                            $(".bookinfo").show();
                            $("#backfromedit").show();
                            $("#submitInfo").show();
                        };

                        window.backfromedit = function() {
                            $("#submitButton").show();
                            $("#addPage").show();
                            $("#editBookInfo").show();
                            $("#ta").show();
                            $("#submitInfo").hide();
                            $(".bookinfo").hide();
                            $("#backfromedit").hide();
                            if (page == numOfPages) $("#next").hide();
                            else $("#next").show();
                            if (page == 1) $("#prev").hide();
                            else $("#prev").show();

                        };
                        window.addPage = function() {
                            $("#next").hide();
                            $("#submitButton").show();
                            document.getElementById("ta").value = "";
                            addpage = numOfPages + 1;
                            page = addpage;
                            numOfPages = addpage;
                            $("#prev").show();
                            $.ajax({
                                url: '/Program_3_war_exploded/storyRest/addpage/'+addpage+'/'+bookID,
                                type: 'PUT'
                            });
                            $.post("/Program_3_war_exploded/storyRest/editStory/"+addpage+"/"+bookID, document.getElementById("ta").value)
                        }
                    })
                })
            });

            window.addStory = function() {
                $("#booktitle").show();
                $("#book").show();
                $("#submitBook").show();
                $("#ta").hide();

                window.submitBook = function () {
                    $.ajax({
                        url: '/Program_3_war_exploded/storyRest/addbook/' + document.getElementById("booktitle").value + "/" + document.getElementById("book").value,
                        type: 'PUT'
                    });
                    document.getElementById("booktitle").value ="";
                    document.getElementById("book").value="";
                    location.reload(true);
                };


            }
        });
    </script>
    <title>${TITLE}</title>
    <style>
        body {
            margin: auto;
            width: 100vw;
            height: 100vh;
            display: flex;
            flex-flow: row wrap;
            align-content: flex-start;
            background-color: #252729;
        }

        #header {
            color: white;
            text-align: left;
            padding: 5px;
            width: 100vw;
        }

        #link {
            line-height: 30px;
            background-color: #66686a;
            width: 20%;
            height: 93vh;
        }

        #content {
            background-color: #a8aaac;
            width: 80%;
            height: 93vh;

        }

        #footer {
            color: white;
            text-align: left;
            padding: 5px;
            width: 100vw;
        }

        textarea {
            width: 100%;
            height: 96%;
        }

        .bookinfo {
            width: 25%;
            height: 2%;
        }
    </style>
</head>
<body>
<div id="header">Welcome</div>
<div id="link" style="color: black">
<#--form to get user info-->
    <form id="submitForm" method='post' action='select'>
        Available Stories:
        <select name="book" id="bookName">
        <#list STORY as STORIES>
            <option>${STORIES}</option>
        <#else>
            <option>No stories...</option>
        </#list>
        </select>
        <br>
        <button id="submit" type="submit" name="page" value="1">Submit</button>
    </form>
    <form id="editForm" class="admin">
        Available Stories to edit:
        <select class="ebooks" id="editBook"></select>
        <button id="edit" type="submit">Edit</button>
    </form>
    <form id="deleteForm" class="admin" >
        Available Stories to delete:
        <select class="ebooks" id="deleteBook"></select>
        <button id="delete" type="submit">Delete</button>
    </form>
    <p id="addstory">Click to add story:  <button id="addStory" onclick="addStory()">Add Story</button></p>

</div>
<div id="content" style="color: black">
    <textarea id="ta"></textarea>
    <textarea id="bookinfot" class="bookinfo" placeholder="Title"></textarea>
    <textarea id="booktitle" class="bookinfo" placeholder="Title"></textarea>
    <textarea id="book" placeholder="Enter book contents here. Put in the `PAGE` `~PAGE` to signify pages."></textarea>
    <button id="next" onclick="nextPage()">Next</button>
    <button id="prev" onclick="prevPage()">Prev</button>
    <button id="submitButton" onclick="submitPage()">Submit</button>
    <button id="addPage" onclick="addPage()">Add Page</button>
    <button id="submitBook" onclick="submitBook()">Submit Book</button>
    <button id="editBookInfo" onclick="editBookInfo()">Edit Book Info</button>
    <button id="submitInfo" onclick="submitInfo()">Submit</button>
    <button id="backfromedit" onclick="backfromedit()">Back</button>

</div>
<div id="footer">Zach Eldemire Reader</div>
</body>
</html>