var socket;

$(document).ready(function() {

  if(!("WebSocket" in window)){
  alert('Need a Browswer that supports Sockets')
  }else{
      //The user has WebSockets

      connect();

      function connect(){
          var host = "ws://localhost:8080/socket";

          try{
              socket = new WebSocket(host);

              message('<p class="event">Socket Status: '+socket.readyState);

              socket.onopen = function(){
             	 message('<p class="event">Socket Status: '+socket.readyState+' (open)');
              }

              socket.onmessage = function(msg){
            	 if (msg.data.indexOf('log-') == 0) 
            		 log('<p>'+msg.data.slice(4));
              }

              socket.onclose = function(){
              	message('<p class="event">Socket Status: '+socket.readyState+' (Closed)');
              }			

          } catch(exception){
             message('<p>Error'+exception);
          }

          socket.sendMsg = function(text){
              try{
            	  if(socket.readyState != 1){
            		  connect();  
            	  } 
            	  socket.send(text);
        		  message('<p class="event">Sent: '+text);

              } catch(exception){
                 message('<p class="warning">');
              }
          }

          function message(msg){
        	 $('#msgPanel').append(msg+'</p>');
          }	
          
          function log(msg){  
         	 $('#logPanel').append(msg+'</p>');
           }
      }//End connect

  }//End else

});