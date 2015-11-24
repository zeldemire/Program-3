<html>
<head>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
    <script>
        var page = ${PAGENUM};
        var end = ${NUMOFPAGES};
        $(document).ready(function(){
            if (page == 1) $("#prev").hide();
            else if (page == end) $("#next").hide();
            else {
                $("#next").show();
                $("#prev").show();
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
    </style>
</head>
<body>
<div id="header">Welcome ${USER} ${EMAIL}</div>
<div id="link" style="color: black">
    <form id="prevForm" action="reader" method="get">
        <input type="hidden" value="${USER}" name="user">
        <input type="hidden" value="${EMAIL}" name="email">
        <input type="hidden" value="${BOOK}" name="book">
        <input type="hidden" value="${PREVPAGE}" name="page">
        <button id="prev">Previous page</button>
    </form>
    <form id="nextForm" action="reader" method="get">
        <input type="hidden" value="${USER}" name="user">
        <input type="hidden" value="${EMAIL}" name="email">
        <input type="hidden" value="${BOOK}" name="book">
        <input type="hidden" value="${NEXTPAGE}" name="page">
        <button id="next" >Next page</button>
    </form>

<#--Add a home button to take you back to story selection-->
</div>
<div id="content" style="color: black">
    <div id="divs">
        <p>${PAGE}</p>
    </div>
</div>
<div id="footer">Zach Eldemire Reader</div>

</body>
</html>