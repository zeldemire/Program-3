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

            /**
             * Tests to see if the user is an admin. If the user is an admin the admin page is shown.
             */
            if(${ADMIN?c}) {
                $(".admin").show();
                $("#prev").hide();
                $("#next").hide();
                $("#bookinfot").hide();
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
                $("#bookinfot").hide();
                $("#booktitle").hide();
                $("#titleSubmit").hide();
                $("#submitBook").hide();
                $("#ta").hide();
                $("#editBookInfo").hide();
                $("#submitInfo").hide();
                $(".bookinfo").hide();
                $("#backfromedit").hide();
                $("#submitForm").show();

            }

            /**
             * This function will load the stories from the database using getJSON.
             */
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

            /**
             * This is the delete form. In this function the submit form default is prevented, and the current selected
             * bookID is sent to the server to delete from the database. It then reloads the page.
             */
            $("#deleteForm").submit(function(evt){
                evt.preventDefault();
                var decision = confirm("Are you sure you want to delete this story?");
                if(decision == true) {
                    var bookName = $("#deleteBook option:selected").text();
                    $.getJSON("/Program_3_war_exploded/storyRest/"+key+"/getStoryID/"+ bookName, function(idres){
                        var deleteInfo = new Object();
                        deleteInfo.bookID = idres.ID;
                        console.log(deleteInfo);
                        $.ajax({
                            url: '/Program_3_war_exploded/storyRest/'+key,
                            type: 'DELETE',
                            contentType: 'application/json',
                            dataType: 'json',
                            data: JSON.stringify(deleteInfo)
                        });
                        location.reload(true);
                    });
                }
            });

            /**
             * This is the edit form. This function handles the editing of the books. The default submission of the form
             * is prevented.
             */
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

                        /**
                         * Handles the next page button. This will check to see if there is another page in the book. If
                         * there isn't another page the next button is hidden.
                         */
                        window.nextPage = function() {
                            page = page + 1;
                            $("#prev").show();
                            if (page == numOfPages) $("#next").hide();
                            $.getJSON("/Program_3_war_exploded/storyRest/"+key+"/story/"+bookID+"/"+page, function(bookContent){
                                document.getElementById("ta").value = bookContent.Story;
                            })
                        };

                        /**
                         * Handles the prev page button. This will check to see if there is a previous page to the book.
                         * If there isn't a previous page the prev button is hidden.
                         */
                        window.prevPage = function() {
                            page = page - 1;
                            $("#next").show();
                            if (page == 1) $("#prev").hide();
                            $.getJSON("/Program_3_war_exploded/storyRest/"+key+"/story/"+bookID+"/"+page, function(bookContent){
                                document.getElementById("ta").value = bookContent.Story;
                            })
                        };

                        /**
                         * Submits the current information on the page to the editstory function on the servlet. Communicates with
                         * servlet using post.
                         */
                        window.submitPage = function() {
                            var editPage = new Object();
                            editPage.newPage = addpage;
                            editPage.bookID = bookID;
                            editPage.content = document.getElementById("ta").value;
                            $.ajax({
                                url: '/Program_3_war_exploded/storyRest/editStory/' + key,
                                type: 'POST',
                                contentType: 'application/json',
                                dataType: 'json',
                                data: JSON.stringify(editPage),
                            });
                            location.reload(true);
                            $("#submitButton").hide();
                        };

                        /**
                         * Submits the current information in the title field to the servlet using post.
                         */
                        window.submitInfo = function() {
                            var titleInfo = new Object();
                            titleInfo.title = document.getElementById("bookinfot").value;
                            titleInfo.bookID = bookID;
                            $.ajax({
                                url: '/Program_3_war_exploded/storyRest/editTitle/' + key,
                                type: 'POST',
                                contentType: 'application/json',
                                dataType: 'json',
                                data: JSON.stringify(editPage),
                            });
                            //$.post("/Program_3_war_exploded/storyRest/editTitle/"+bookID+"/"+document.getElementById("bookinfot").value);
                            location.reload(true);
                        };

                        /**
                         * Function will hide all of the other text areas and buttons except those pertaining to the editing
                         * of the title.
                         */
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
                            $("#bookinfot").show();
                            $("#backfromedit").show();
                            $("#submitInfo").show();
                        };

                        /**
                         * Back button to go back to the editing of the page contents page.
                         */
                        window.backfromedit = function() {
                            $("#submitButton").show();
                            $("#addPage").show();
                            $("#editBookInfo").show();
                            $("#ta").show();
                            $("#submitInfo").hide();
                            $("#bookinfot").hide();
                            $("#backfromedit").hide();
                            if (page == numOfPages) $("#next").hide();
                            else $("#next").show();
                            if (page == 1) $("#prev").hide();
                            else $("#prev").show();

                        };

                        /**
                         * Sends the added page to the servlet using put, identifying the new page limit using post.
                         */
                        window.addPage = function() {
                            $("#next").hide();
                            $("#submitButton").show();
                            document.getElementById("ta").value = "";
                            addpage = numOfPages + 1;
                            page = addpage;
                            numOfPages = addpage;
                            $("#prev").show();
                            var addPage = new Object();
                            addPage.newPage = addpage;
                            addPage.bookID = bookID;
                            addPage.content = document.getElementById("ta").value;
                            $.ajax({
                                url: '/Program_3_war_exploded/storyRest/addpage/' + key,
                                type: 'PUT',
                                contentType: 'application/json',
                                dataType: 'json',
                                data: JSON.stringify(addPage)
                            });
                            $.ajax({
                                url: '/Program_3_war_exploded/storyRest/editStory/' + key,
                                type: 'POST',
                                contentType: 'application/json',
                                dataType: 'json',
                                data: JSON.stringify(addPage)
                            });

                            //$.post("/Program_3_war_exploded/storyRest/editStory/"+addpage+"/"+bookID, document.getElementById("ta").value)
                        }
                    })
                })
            });

            /**
             * Shows the relevant buttons for adding a story. Title field and book field.
             */
            window.addStory = function() {
                $("#prev").hide();
                $("#next").hide();
                $("#ta").hide();
                $("#submitButton").hide();
                $("#addPage").hide();
                $("#titleSubmit").hide();
                $("#editBookInfo").hide();
                $("#booktitle").show();
                $("#book").show();
                $("#submitBook").show();

                /**
                 * Submits the information in the title and book field using put.
                 */
                window.submitBook = function () {
                    var test = new Object();
                    test.title = $('#booktitle').val();
                    test.page = $('#book').val();

                    $.ajax({
                        url: '/Program_3_war_exploded/storyRest/addbook/'+ key,
                        type: 'PUT',
                        contentType: 'application/json',
                        dataType: 'json',
                        data: JSON.stringify(test)

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
<div id="header">Welcome ${USER} ${EMAIL}</div>
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
        <input type="hidden" value="${USER}" name="user">
        <input type="hidden" value="${EMAIL}" name="email">
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