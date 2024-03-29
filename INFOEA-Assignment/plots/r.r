require(Hmisc)
require(data.table)
require(ggplot2)
require(gridExtra)

#inputFileName = 'runtrace-2012-01-30-13.csv'
widthDetails.legendGrob <- function(x) unit(2.5, "cm")

getDataTable <- function(inputFileName){
	data <- csv.get(inputFileName, sep=';')
	# eerste indexwaarde eruitgooien, dit maakt sommige grafiekjes duidelijker
	data <- subset(data, Index > 0)
	return(data.table(data) )
}

getGrouped <- function(inputFileName) {
	dataTable <- getDataTable(inputFileName)
	grouped <- dataTable[, list(Fitness=mean(Fitness)), by=list(Problem, Heuristic, Instance, Index)]
	return(grouped)
}

getInstancesPlot <- function(problem='SAT', inputFileName){
	grouped <- getGrouped(inputFileName)
	
	instances <- unique(grouped$Instance)
	problems <- unique(grouped$Problem)
	heuristics <- unique(grouped$Heuristic)
	nHeuristics = length(heuristics)
	
	plots <- list()
	last <- NULL
	
	for(i in instances) {
	  p <- ggplot(grouped, aes(Index, Fitness, colour=factor(Heuristic)))
	
		for (h in heuristics) {
		  heur <- grouped[Heuristic==h][Instance==i][Problem==problem]
		  p <- p + geom_line(aes(Index, Fitness), data=heur)
		}
	  plots[[toString(i)]] <- p + opts(legend.position="none")
	  last <- p
	}
	
	# Legend
	leg <- ggplotGrob(last + opts(keep="legend_box") + labs(colour = "Heuristics"))
	## one needs to provide the legend with a well-defined width
	legend=gTree(children=gList(leg), cl="legendGrob")
	plots[["legend"]] <- legend
	plots[["main"]] <- problem
	
	return(plots)
}

plotInstances <- function(problem='SAT', inputFileName){
	return(do.call(arrangeGrob, getInstancesPlot(problem, inputFileName)))
}

generateBoxPlot <- function(problem='SAT', dataTable){
	boxPlotData <- dataTable[Problem==problem]
	boxPlot <- qplot(boxPlotData$Heuristic, boxPlotData$Fitness, geom=c("boxplot"), xlab = "Heuristic", ylab = "Fitness", main = problem) + geom_jitter(position=position_jitter(w=0.3, h=0.1), aes(colour=boxPlotData$Heuristic), alpha=0.15) + opts(legend.position="none")

	return(boxPlot)
}

plotBoxPlots <- function(inputFileName) {
	dataTable <- getDataTable(inputFileName)
	problems <- unique(dataTable $Problem)
	plots <- list()
	for(p in problems){
		plots[[toString(p)]] <- generateBoxPlot(p, dataTable) 
	}
	#plots[["main"]] <- "BoxPlots"
	do.call(grid.arrange, plots)
}

outputBoxPlotsToPdf <- function(inputFileName) {
	pdf(paste(inputFileName,'BoxPlots.pdf',sep=''), paper='a4')
	plotBoxPlots(inputFileName)
	dev.off()
}

outputToPdf <- function(inputFileName) {
	problems <- unique(grouped$Problem)
	pdf(paste(inputFileName,'.pdf',sep=''), paper='a4')
	for(p in problems){
		do.call(grid.arrange, getInstancesPlot(p, inputFileName))
	}
	dev.off()
}
