

  //ADOPTED VS NOT ADOPTED

  var adopted = d3.selectAll('#adopted')["_groups"]["0"]
  //var nAdopted = d3.selectAll('#typeNA')["_groups"]["0"].length

  var types = [];
  for (i = 0; i < adopted.length; i++) {
      types.push(adopted.item(i).textContent);
  }

  var trueNum = 0;
  var falseNum = 0;
  for (i = 0; i < types.length; i++) {
      if(types[i].toString().localeCompare('true') == 0){
          trueNum++;
      }
      else if(types[i].toString().localeCompare('false') == 0){
          falseNum++;
      }
  }

  var typesDataset = []

  typesDataset.push(trueNum);
  typesDataset.push(falseNum);

    var svgWidth = 340, svgHeight = 340, barPadding = 20;
    var barWidth = (svgWidth / typesDataset.length);


    var svg = d3.select('.adoptedTypes')
      .attr("width", svgWidth)
        .attr("height", svgHeight);


    var yScaleNew = d3.scaleLinear()
        .domain([0, d3.max(typesDataset)])
        .range([ 0, svgHeight*0.9]);


    var barChart = svg.selectAll("rect")
        .data(typesDataset)
        .enter()
        .append("rect")
        .attr("id", "typeRect")
        .attr("y", function(d){
            return svgHeight - yScaleNew(d);
        })
        .attr("height", function(d){
            return yScaleNew (d);
        })
        .attr("width", barWidth - barPadding)
        .attr("transform", function (d, i){
            var translate = [barWidth * i + 20, -3]; //menuva od kaj pochnuvaat barovite
            return "translate(" + translate +")";
        });

    d3.selectAll("rect")
        .style('fill', function(d) {
            if(d >= 17) {
                return '#709acd';
            } else {
                return '#9b11ce';
            }
        });


    var first = false;
    var text = svg.selectAll("text")
        .data(typesDataset)
        .enter()
        .append("text")
        .text(function(d) {
            if(first == false){
                first = true;
                return "ADOPTED";
            }
            else {
                return "NOT ADOPTED";
            }
        })
        .attr("y", function(d, i) {
            return svgHeight - d -20; //spushta so + labels kreva so -
        })
        .attr("x", function(d, i) {
            return barWidth * i + 60;
        })
        .attr("fill", "white")
        .attr("z-index", 100)
        .attr("font-size",12);



        var yScale = d3.scaleLinear()
              .domain([-2.1, d3.max(typesDataset)])
              .range([ svgHeight, 0]);
          var y_axis = d3.axisLeft()
              .scale(yScale)

          svg.append("g")
              .attr("transform", "translate(20, 10)")
              .call(y_axis);












