void test(int queue, int first_sibling, int nr_queues, int nr_uniq_cpus, int i){
if ( first_sibling  ==  i     )     {  map [   i ] =  cpu_to_queue_index ( nr_uniq_cpus       , nr_queues       , queue       )    ;  queue ++   ;  }    }