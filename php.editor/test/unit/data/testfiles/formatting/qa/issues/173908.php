<?php

class Test {
public function test(){
        try
        {
    if(true)
    {
throw new Exception('error');
    }
    foreach($aa as $a)
    {
    }
        }
        catch(Exception $e)
        {
            $this->_flashMessenger->addMessage('Chyba: '.$e);
        }
    }
}
?>