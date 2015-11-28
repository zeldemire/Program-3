<html>
<head>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
    <script>
        $(document).ready(function(){
            if(${ADMIN}) $("#edit").show();
            else $("#edit").hide();
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
            text-align: center;
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
<div id="header">Welcome to the story reader web app</div>
<div id="link"><h1>Login to Web App</h1>
    <button id="edit" onclick="edit()">Edit</button>
</div>
<div id="content"></div>
<div id="footer">Zach Eldemire Reader</div>
</body>
</html>