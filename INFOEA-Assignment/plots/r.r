require(Hmisc)
require(data.table)
require(ggplot2)

data <- csv.get('test.csv', sep=';')
dataTable <- data.table(c) 
grouped <- dataTable[, list(Instance=Instance, Fitness=mean(Fitness)), by=list(Problem, Heuristic, Index)]

instance <- 0
problem <- 'SAT'

heuristics = c('ILS', 'GLS', 'AGLS')
nHeuristics = length(heuristics)

p <- ggplot(grouped, aes(Index, Fitness, colour=factor(Heuristic)))

for (h in heuristics) {
  heur <- grouped[Heuristic==h,][Instance==instance,][Problem==problem,]
  p <- p + geom_line(aes(Index, Fitness), data=heur)
}

p <- p + opts(title = paste(problem, ", instance ", instance, sep=""))
