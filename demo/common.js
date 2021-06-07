// // 매개변수의 타압은 문자열로 한정한다.
// function move(param1, param2){
//     console.log('move', param1, param2);
// }

// function input(ask){
//     console.log('input', ask);
// }

// function foo(param){
//     console.log(';f');
// }

// function scrollDown(){
//     scrollTo(document.documentElement.scrollTop, document.documentElement.scrollTop+50);
// }

function scrollUp(){
    scrollTo(document.documentElement.scrollTop, document.documentElement.scrollTop-50);
}

function refresh(){
    location.reload();
}

function zoomIn(){
    if(document.body.style.zoom==""){
        document.body.style.zoom = 110+"%";
    }else{
        var zoom = document.body.style.zoom.toString().substring(0, document.body.style.zoom.toString().indexOf("%"));
        console.log(zoom);
        document.body.style.zoom = (parseInt(zoom)+10)+"%";
    }
}

// function zoomOut(){
//     if(document.body.style.zoom==""){
//         document.body.style.zoom = 90+"%";
//     }else{
//         var zoom = document.body.style.zoom.toString().substring(0, document.body.style.zoom.toString().indexOf("%"));
//         console.log(zoom);
//         document.body.style.zoom = (parseInt(zoom)-10)+"%";
//     }
// }

// function goPreviousPage(){
//     history.back();
// }

// function goNextPage(){
//     history.forward();
// }