require(Hmisc)
require(data.table)
require(ggplot2)
require(gridExtra)

#loadData <- function(){
inputFileName = 'runtrace-2012-01-16-0.csv'
data <- csv.get(inputFileName, sep=';')

# eerste indexwaarde eruitgooien, dit maakt sommige grafiekjes duidelijker
data <- subset(data, Index > 0)
dataTable <- data.table(data) 
grouped <- dataTable[, list(Fitness=mean(Fitness)), by=list(Problem, Heuristic, Instance, Index)]

getInstancesPlot <- function(problem='SAT'){
	instances <- unique(grouped$Instance)
	problems <- unique(grouped$Problem)
	heuristics <- unique(grouped$Heuristic)
	nHeuristics = length(heuristics)
	
	plots <- list()
	
	for(i in instances) {
	  p <- ggplot(grouped, aes(Index, Fitness, colour=factor(Heuristic)))
	
		for (h in heuristics) {
		  heur <- grouped[Heuristic==h][Instance==i][Problem==problem]
		  p <- p + geom_line(aes(Index, Fitness), data=heur)
		}
	plots[[toString(i)]] <- p + opts(legend.position="none")
	}
	
	# Legend
	leg <- ggplotGrob(p + opts(keep="legend_box") + labs(colour = "Heuristics"))
	## one needs to provide the legend with a well-defined width
	legend=gTree(children=gList(leg), cl="legendGrob")
	widthDetails.legendGrob <- function(x) unit(2.5, "cm")
	plots[["legend"]] <- legend
	plots[["main"]] <- problem
	
	return(plots)
}

plotInstances <- function(problem='SAT'){
	do.call(grid.arrange, getInstancesPlot(problem))
}

outputToPdf <- function() {
	problems <- unique(grouped$Problem)
	pdf(paste(inputFileName,'.pdf',sep=''))
	for(p in problems){
		do.call(grid.arrange, getInstancesPlot(p))
	}
	dev.off()
}